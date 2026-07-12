import { useState, useEffect } from 'react';
import {
  useAllAuditCycles,
  useCreateAuditCycle,
  useAuditChecklist,
  useSubmitAuditFinding,
  useCloseAuditCycle,
  useEmployees
} from '../hooks/useOperations';
import { useDepartments } from '../hooks/useDepartments';

export default function AuditPage() {
  const { data: cycles = [], refetch: refetchCycles } = useAllAuditCycles();
  const { data: employees = [] } = useEmployees();
  const { data: departments = [] } = useDepartments();

  const createMutation = useCreateAuditCycle();
  const submitFindingMutation = useSubmitAuditFinding();
  const closeMutation = useCloseAuditCycle();

  // Create Cycle State
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [cycleForm, setCycleForm] = useState({
    name: '',
    startDate: '',
    endDate: '',
    auditorIds: [],
    targetDepartmentId: '',
    targetLocation: ''
  });

  // Selected Cycle State
  const [selectedCycleId, setSelectedCycleId] = useState('');
  const { data: checklist = [], refetch: refetchChecklist } = useAuditChecklist(selectedCycleId);

  useEffect(() => {
    if (selectedCycleId) {
      refetchChecklist();
    }
  }, [selectedCycleId]);

  const handleCreateCycle = (e) => {
    e.preventDefault();
    const payload = {
      name: cycleForm.name,
      startDate: cycleForm.startDate,
      endDate: cycleForm.endDate,
      auditorIds: cycleForm.auditorIds.map(Number),
      targetDepartmentId: cycleForm.targetDepartmentId ? Number(cycleForm.targetDepartmentId) : null,
      targetLocation: cycleForm.targetLocation || null
    };

    createMutation.mutate(payload, {
      onSuccess: () => {
        alert('Audit cycle created successfully.');
        setShowCreateModal(false);
        setCycleForm({
          name: '',
          startDate: '',
          endDate: '',
          auditorIds: [],
          targetDepartmentId: '',
          targetLocation: ''
        });
        refetchCycles();
      }
    });
  };

  const handleAuditorSelection = (e) => {
    const options = e.target.options;
    const selected = [];
    for (let i = 0; i < options.length; i++) {
      if (options[i].selected) {
        selected.push(options[i].value);
      }
    }
    setCycleForm({ ...cycleForm, auditorIds: selected });
  };

  const handleAuditAsset = (assetId, status) => {
    if (!selectedCycleId) return;
    submitFindingMutation.mutate({
      cycleId: Number(selectedCycleId),
      finding: {
        assetId,
        status,
        notes: `Asset verified as ${status} during audit cycle.`
      }
    }, {
      onSuccess: () => {
        refetchChecklist();
      }
    });
  };

  const handleCloseCycle = () => {
    if (!selectedCycleId) return;
    if (window.confirm('Are you sure you want to close this audit cycle? This will lock all findings and cascade asset status updates (e.g. Missing -> LOST, Damaged -> DAMAGED).')) {
      closeMutation.mutate(Number(selectedCycleId), {
        onSuccess: () => {
          alert('Audit cycle closed and locked.');
          refetchCycles();
        }
      });
    }
  };

  const selectedCycle = cycles.find((c) => c.id === Number(selectedCycleId));
  const missingCount = checklist.filter((item) => item.status === 'MISSING').length;
  const damagedCount = checklist.filter((item) => item.status === 'DAMAGED').length;

  return (
    <div className="page">
      <div className="page-header">
        <h2>Asset Audit Cycles</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + Create Audit Cycle
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '32px' }}>
        {/* Left Side: Cycles List */}
        <div>
          <h3>Audit Cycles</h3>
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Cycle Name</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {cycles.map((c) => (
                  <tr
                    key={c.id}
                    onClick={() => setSelectedCycleId(c.id.toString())}
                    style={{ cursor: 'pointer', background: selectedCycleId === c.id.toString() ? '#202029' : 'transparent' }}
                  >
                    <td>
                      <strong>{c.name}</strong>
                      <div style={{ fontSize: '12px', color: '#94a3b8' }}>
                        {c.startDate} to {c.endDate}
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${c.status === 'ACTIVE' ? 'badge-success' : 'badge-neutral'}`}>
                        {c.status}
                      </span>
                    </td>
                  </tr>
                ))}
                {cycles.length === 0 && (
                  <tr>
                    <td colSpan="2" style={{ textAlign: 'center', color: '#94a3b8' }}>No audit cycles found.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Right Side: Checklist Table */}
        <div>
          {selectedCycle ? (
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                <h3>Checklist for: {selectedCycle.name}</h3>
                {selectedCycle.status === 'ACTIVE' && (
                  <button className="btn btn-danger" onClick={handleCloseCycle}>
                    Close Audit Cycle
                  </button>
                )}
              </div>

              {(missingCount > 0 || damagedCount > 0) && (
                <div style={{ background: 'rgba(245, 158, 11, 0.1)', border: '1px solid #f59e0b', borderRadius: '8px', padding: '12px 16px', color: '#fef3c7', marginBottom: '16px', fontSize: '13px' }}>
                  ⚠️ <strong>Discrepancy Summary:</strong> {missingCount} asset(s) flagged as MISSING, {damagedCount} flagged as DAMAGED. Closing this cycle will auto-reconcile asset inventories.
                </div>
              )}

              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Asset</th>
                      <th>Expected Location</th>
                      <th>Audited Status</th>
                      {selectedCycle.status === 'ACTIVE' && <th>Verify Actions</th>}
                    </tr>
                  </thead>
                  <tbody>
                    {checklist.map((item) => (
                      <tr key={item.assetId}>
                        <td>
                          <strong>{item.assetTag}</strong>
                          <div style={{ fontSize: '12px', color: '#94a3b8' }}>{item.assetName}</div>
                        </td>
                        <td>{item.expectedLocation ?? 'Unassigned'}</td>
                        <td>
                          {item.status ? (
                            <span className={`badge ${item.status === 'VERIFIED' ? 'badge-success' : item.status === 'MISSING' ? 'badge-danger' : 'badge-warning'}`}>
                              {item.status}
                            </span>
                          ) : (
                            <span className="badge badge-neutral">PENDING</span>
                          )}
                        </td>
                        {selectedCycle.status === 'ACTIVE' && (
                          <td>
                            <div style={{ display: 'flex', gap: '4px' }}>
                              <button className="btn btn-primary" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleAuditAsset(item.assetId, 'VERIFIED')}>
                                Verify
                              </button>
                              <button className="btn btn-danger" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleAuditAsset(item.assetId, 'MISSING')}>
                                Missing
                              </button>
                              <button className="btn btn-secondary" style={{ padding: '4px 8px', fontSize: '11px' }} onClick={() => handleAuditAsset(item.assetId, 'DAMAGED')}>
                                Damage
                              </button>
                            </div>
                          </td>
                        )}
                      </tr>
                    ))}
                    {checklist.length === 0 && (
                      <tr>
                        <td colSpan={selectedCycle.status === 'ACTIVE' ? '4' : '3'} style={{ textAlign: 'center', color: '#94a3b8' }}>
                          No assets fall under this audit cycle's scope.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          ) : (
            <p style={{ color: '#94a3b8' }}>Select an audit cycle on the left to start checking assets.</p>
          )}
        </div>
      </div>

      {/* Create Modal */}
      {showCreateModal && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <h3>Create Audit Cycle</h3>
            <form onSubmit={handleCreateCycle} style={{ width: '100%', border: 'none', padding: '0', background: 'none' }}>
              <label>
                Cycle Name *
                <input
                  type="text"
                  placeholder="E.g. Q3 Electronics Check"
                  value={cycleForm.name}
                  onChange={(e) => setCycleForm({ ...cycleForm, name: e.target.value })}
                  required
                />
              </label>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <label>
                  Start Date *
                  <input type="date" value={cycleForm.startDate} onChange={(e) => setCycleForm({ ...cycleForm, startDate: e.target.value })} required />
                </label>
                <label>
                  End Date *
                  <input type="date" value={cycleForm.endDate} onChange={(e) => setCycleForm({ ...cycleForm, endDate: e.target.value })} required />
                </label>
              </div>
              <label>
                Assigned Auditors (Select multiple with Ctrl) *
                <select multiple value={cycleForm.auditorIds} onChange={handleAuditorSelection} required style={{ height: '80px' }}>
                  {employees
                    .filter((e) => e.role === 'ADMIN' || e.role === 'ASSET_MANAGER')
                    .map((e) => (
                      <option key={e.id} value={e.id}>{e.fullName}</option>
                    ))}
                </select>
              </label>
              <label>
                Scope Department
                <select value={cycleForm.targetDepartmentId} onChange={(e) => setCycleForm({ ...cycleForm, targetDepartmentId: e.target.value })}>
                  <option value="">All Departments</option>
                  {departments.map((d) => (
                    <option key={d.id} value={d.id}>{d.name}</option>
                  ))}
                </select>
              </label>
              <label>
                Scope Location
                <input
                  type="text"
                  placeholder="E.g. Office 302"
                  value={cycleForm.targetLocation}
                  onChange={(e) => setCycleForm({ ...cycleForm, targetLocation: e.target.value })}
                />
              </label>
              <div className="dialog-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
