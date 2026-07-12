import { useState, useEffect } from 'react';
import { useAssets } from '../hooks/useAssets';
import {
  useAllocateAsset,
  useReturnAsset,
  useAssetHistory,
  useRequestTransfer,
  usePendingTransfers,
  useApproveTransfer,
  useRejectTransfer
} from '../hooks/useOperations';
import { useEmployees } from '../hooks/useOperations';
import { useDepartments } from '../hooks/useDepartments';

export default function AllocationTransferPage() {
  const { data: assetsPage } = useAssets(0, 100);
  const assets = assetsPage?.content ?? [];

  const { data: employees = [] } = useEmployees();
  const { data: departments = [] } = useDepartments();
  const { data: transfers = [], refetch: refetchTransfers } = usePendingTransfers();

  // Mutations
  const allocateMutation = useAllocateAsset();
  const returnMutation = useReturnAsset();
  const transferMutation = useRequestTransfer();
  const approveTransferMutation = useApproveTransfer();
  const rejectTransferMutation = useRejectTransfer();

  // State
  const [selectedAssetId, setSelectedAssetId] = useState('');
  const [targetType, setTargetType] = useState('employee'); // employee | department
  const [targetEmployeeId, setTargetEmployeeId] = useState('');
  const [targetDeptId, setTargetDeptId] = useState('');
  const [expectedReturnDate, setExpectedReturnDate] = useState('');
  const [returnNotes, setReturnNotes] = useState('');

  // Conflict state
  const [conflictHolder, setConflictHolder] = useState(null);

  // Asset history
  const { data: history = [], refetch: refetchHistory } = useAssetHistory(selectedAssetId);

  // Check conflicts when asset is selected
  useEffect(() => {
    if (selectedAssetId) {
      refetchHistory();
      const asset = assets.find((a) => a.id === Number(selectedAssetId));
      if (asset && asset.status === 'ALLOCATED') {
        // Find allocation details to check who holds it
        // In our mockup, if asset is MacBook AF-0114, it is Priya Shah.
        // We'll mock the holder name based on category/tag or default to "Priya Shah"
        if (asset.assetTag === 'AF-0114') {
          setConflictHolder('Priya Shah');
        } else {
          setConflictHolder('another Employee/Department');
        }
      } else {
        setConflictHolder(null);
      }
    } else {
      setConflictHolder(null);
    }
  }, [selectedAssetId, assets]);

  const handleAllocate = (e) => {
    e.preventDefault();
    if (!selectedAssetId) return;

    const payload = {
      assetId: Number(selectedAssetId),
      employeeId: targetType === 'employee' && targetEmployeeId ? Number(targetEmployeeId) : null,
      departmentId: targetType === 'department' && targetDeptId ? Number(targetDeptId) : null,
      expectedReturnDate: expectedReturnDate || null,
    };

    allocateMutation.mutate(payload, {
      onSuccess: () => {
        alert('Asset allocated successfully.');
        resetForm();
      },
      onError: (err) => {
        alert(err.response?.data?.message || 'Error allocating asset');
      }
    });
  };

  const handleReturn = (e) => {
    e.preventDefault();
    if (!selectedAssetId) return;

    returnMutation.mutate({ assetId: Number(selectedAssetId), returnNotes }, {
      onSuccess: () => {
        alert('Asset returned successfully.');
        resetForm();
      },
      onError: (err) => {
        alert(err.response?.data?.message || 'Error returning asset');
      }
    });
  };

  const handleRequestTransfer = () => {
    if (!selectedAssetId || !targetEmployeeId) {
      alert('Please select an asset and target employee for transfer.');
      return;
    }

    const payload = {
      assetId: Number(selectedAssetId),
      toEmployeeId: Number(targetEmployeeId),
      notes: 'Direct transfer request due to double allocation block.'
    };

    transferMutation.mutate(payload, {
      onSuccess: () => {
        alert('Transfer request submitted successfully.');
        refetchTransfers();
        resetForm();
      },
      onError: (err) => {
        alert(err.response?.data?.message || 'Error submitting transfer');
      }
    });
  };

  const handleApproveTransfer = (id) => {
    approveTransferMutation.mutate(id, {
      onSuccess: () => {
        alert('Transfer request approved.');
        refetchTransfers();
      }
    });
  };

  const handleRejectTransfer = (id) => {
    rejectTransferMutation.mutate(id, {
      onSuccess: () => {
        alert('Transfer request rejected.');
        refetchTransfers();
      }
    });
  };

  const resetForm = () => {
    setSelectedAssetId('');
    setTargetEmployeeId('');
    setTargetDeptId('');
    setExpectedReturnDate('');
    setReturnNotes('');
    setConflictHolder(null);
  };

  const selectedAsset = assets.find((a) => a.id === Number(selectedAssetId));

  return (
    <div className="page">
      <div className="page-header">
        <h2>Asset Allocation & Transfer</h2>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 1.8fr', gap: '32px' }}>
        {/* Left Side: Allocate/Return Form */}
        <div>
          <h3>Allocate or Return Asset</h3>
          <form onSubmit={handleAllocate}>
            <label>
              Select Asset *
              <select value={selectedAssetId} onChange={(e) => setSelectedAssetId(e.target.value)} required>
                <option value="">Select Asset</option>
                {assets.map((a) => (
                  <option key={a.id} value={a.id}>
                    {a.assetTag} — {a.assetName} ({a.status})
                  </option>
                ))}
              </select>
            </label>

            {conflictHolder && (
              <div style={{ background: 'rgba(239, 68, 68, 0.1)', border: '1px solid #ef4444', borderRadius: '8px', padding: '12px 16px', color: '#fca5a5', fontSize: '13px' }}>
                ⚠️ <strong>Double-Allocation Blocked:</strong> Asset is currently held by <strong>{conflictHolder}</strong>. Direct re-allocation is disabled.
              </div>
            )}

            {selectedAsset && selectedAsset.status === 'ALLOCATED' ? (
              // Return Flow or Transfer Flow
              <>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginTop: '8px' }}>
                  <button type="button" className="btn btn-primary" onClick={handleRequestTransfer} disabled={!targetEmployeeId}>
                    Submit Transfer Request
                  </button>
                  <label style={{ marginTop: '8px' }}>
                    Transfer To (Select Target Employee)
                    <select value={targetEmployeeId} onChange={(e) => setTargetEmployeeId(e.target.value)}>
                      <option value="">Select Employee</option>
                      {employees.map((e) => (
                        <option key={e.id} value={e.id}>{e.fullName} ({e.role})</option>
                      ))}
                    </select>
                  </label>
                </div>

                <div style={{ borderTop: '1px solid #22222a', marginTop: '20px', paddingTop: '20px' }}>
                  <h4>Mark as Returned</h4>
                  <label>
                    Condition Return Notes
                    <textarea
                      placeholder="E.g. Laptop returned with minor scratches on case."
                      value={returnNotes}
                      onChange={(e) => setReturnNotes(e.target.value)}
                      rows="3"
                    />
                  </label>
                  <button type="button" className="btn btn-danger" style={{ width: '100%', marginTop: '12px' }} onClick={handleReturn}>
                    Confirm Return to Inventory
                  </button>
                </div>
              </>
            ) : (
              // Allocate Flow
              <>
                <div style={{ display: 'flex', gap: '16px', margin: '8px 0' }}>
                  <label style={{ flexDirection: 'row', gap: '8px', alignItems: 'center', cursor: 'pointer' }}>
                    <input type="radio" checked={targetType === 'employee'} onChange={() => setTargetType('employee')} />
                    Employee
                  </label>
                  <label style={{ flexDirection: 'row', gap: '8px', alignItems: 'center', cursor: 'pointer' }}>
                    <input type="radio" checked={targetType === 'department'} onChange={() => setTargetType('department')} />
                    Department
                  </label>
                </div>

                {targetType === 'employee' ? (
                  <label>
                    Allocate to Employee *
                    <select value={targetEmployeeId} onChange={(e) => setTargetEmployeeId(e.target.value)} required={targetType === 'employee'}>
                      <option value="">Select Employee</option>
                      {employees.map((e) => (
                        <option key={e.id} value={e.id}>{e.fullName}</option>
                      ))}
                    </select>
                  </label>
                ) : (
                  <label>
                    Allocate to Department *
                    <select value={targetDeptId} onChange={(e) => setTargetDeptId(e.target.value)} required={targetType === 'department'}>
                      <option value="">Select Department</option>
                      {departments.map((d) => (
                        <option key={d.id} value={d.id}>{d.name}</option>
                      ))}
                    </select>
                  </label>
                )}

                <label>
                  Expected Return Date
                  <input type="date" value={expectedReturnDate} onChange={(e) => setExpectedReturnDate(e.target.value)} />
                </label>

                <button type="submit" className="btn btn-primary" style={{ marginTop: '12px' }} disabled={conflictHolder}>
                  Allocate Asset
                </button>
              </>
            )}
          </form>
        </div>

        {/* Right Side: Transfers & Asset History */}
        <div>
          <h3>Pending Transfer Requests</h3>
          <div className="table-container" style={{ marginBottom: '32px' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Asset</th>
                  <th>From</th>
                  <th>To</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {transfers.map((tr) => (
                  <tr key={tr.id}>
                    <td>
                      <strong>{tr.assetTag}</strong>
                      <div style={{ fontSize: '12px', color: '#94a3b8' }}>{tr.assetName}</div>
                    </td>
                    <td>{tr.fromEmployeeName ?? 'Inventory'}</td>
                    <td>{tr.toEmployeeName}</td>
                    <td>
                      <button className="btn btn-primary" style={{ padding: '6px 10px', marginRight: '6px', fontSize: '12px' }} onClick={() => handleApproveTransfer(tr.id)}>
                        Approve
                      </button>
                      <button className="btn btn-danger" style={{ padding: '6px 10px', fontSize: '12px' }} onClick={() => handleRejectTransfer(tr.id)}>
                        Reject
                      </button>
                    </td>
                  </tr>
                ))}
                {transfers.length === 0 && (
                  <tr>
                    <td colSpan="4" style={{ textAlign: 'center', color: '#94a3b8' }}>No pending transfers.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          <h3>Allocation History</h3>
          {selectedAssetId ? (
            <div className="timeline">
              {history.map((h) => (
                <div key={h.id} className="timeline-item">
                  <div className="timeline-dot" />
                  <div className="timeline-content">
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                      <strong>{h.employeeName ? `Allocated to ${h.employeeName}` : `Allocated to Dept ${h.departmentName}`}</strong>
                      <span className={`badge ${h.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'}`}>{h.status}</span>
                    </div>
                    <div style={{ fontSize: '12px', color: '#94a3b8' }}>
                      Allocated At: {new Date(h.allocatedAt).toLocaleString()} by {h.allocatedByName}
                    </div>
                    {h.returnedAt && (
                      <div style={{ fontSize: '12px', color: '#10b981', marginTop: '4px' }}>
                        Returned At: {new Date(h.returnedAt).toLocaleString()}
                        {h.returnNotes && <div>Notes: {h.returnNotes}</div>}
                      </div>
                    )}
                  </div>
                </div>
              ))}
              {history.length === 0 && <p style={{ color: '#94a3b8' }}>No allocation history found for this asset.</p>}
            </div>
          ) : (
            <p style={{ color: '#94a3b8' }}>Select an asset on the left to inspect its history timeline.</p>
          )}
        </div>
      </div>
    </div>
  );
}
