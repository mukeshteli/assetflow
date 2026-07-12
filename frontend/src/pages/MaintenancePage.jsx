import { useState } from 'react';
import { useAssets } from '../hooks/useAssets';
import {
  useAllMaintenance,
  useCreateMaintenance,
  useUpdateMaintenance
} from '../hooks/useOperations';

const STAGES = [
  { key: 'PENDING', label: 'Pending' },
  { key: 'APPROVED', label: 'Approved' },
  { key: 'TECHNICIAN_ASSIGNED', label: 'Technician Assigned' },
  { key: 'IN_PROGRESS', label: 'In Progress' },
  { key: 'RESOLVED', label: 'Resolved' }
];

export default function MaintenancePage() {
  const { data: tickets = [], refetch } = useAllMaintenance();
  const { data: assetsPage } = useAssets(0, 100);
  const assets = assetsPage?.content ?? [];

  const createMutation = useCreateMaintenance();
  const updateMutation = useUpdateMaintenance();

  // Create modal state
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newRequest, setNewRequest] = useState({ assetId: '', description: '', priority: 'MEDIUM' });

  // Details modal state
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [techName, setTechName] = useState('');
  const [notes, setNotes] = useState('');

  const handleOpenDetails = (ticket) => {
    setSelectedTicket(ticket);
    setTechName(ticket.technicianName ?? '');
    setNotes(ticket.notes ?? '');
  };

  const handleCreate = (e) => {
    e.preventDefault();
    createMutation.mutate({
      assetId: Number(newRequest.assetId),
      description: newRequest.description,
      priority: newRequest.priority
    }, {
      onSuccess: () => {
        alert('Maintenance request raised successfully.');
        setShowCreateModal(false);
        setNewRequest({ assetId: '', description: '', priority: 'MEDIUM' });
        refetch();
      }
    });
  };

  const handleAction = (status) => {
    if (!selectedTicket) return;
    updateMutation.mutate({
      id: selectedTicket.id,
      payload: {
        status,
        technicianName: techName,
        notes
      }
    }, {
      onSuccess: () => {
        alert(`Ticket advanced to ${status}.`);
        setSelectedTicket(null);
        refetch();
      }
    });
  };

  // Group tickets by status
  const groupedTickets = STAGES.reduce((acc, stage) => {
    acc[stage.key] = tickets.filter((t) => t.status === stage.key);
    return acc;
  }, {});

  return (
    <div className="page">
      <div className="page-header">
        <h2>Maintenance Kanban</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          + Raise Request
        </button>
      </div>

      <div className="design-note">
        <strong>Workflow Note:</strong> Approving a pending request moves the asset status to <strong>Under Maintenance</strong>. Resolving a request returns it to <strong>Available</strong>.
      </div>

      {/* Kanban Board Layout */}
      <div className="kanban-board">
        {STAGES.map((stage) => (
          <div key={stage.key} className="kanban-col">
            <h3>
              {stage.label}
              <span>({groupedTickets[stage.key]?.length ?? 0})</span>
            </h3>
            <div className="kanban-cards">
              {groupedTickets[stage.key]?.map((ticket) => (
                <div key={ticket.id} className="kanban-card" onClick={() => handleOpenDetails(ticket)}>
                  <div className="kanban-card-title">{ticket.assetName}</div>
                  <div style={{ fontSize: '11px', color: '#94a3b8', marginBottom: '8px' }}>
                    Tag: {ticket.assetTag}
                  </div>
                  <div className="kanban-card-desc">{ticket.description}</div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span className={`badge ${ticket.priority === 'CRITICAL' || ticket.priority === 'HIGH' ? 'badge-danger' : 'badge-warning'}`}>
                      {ticket.priority}
                    </span>
                    <span style={{ fontSize: '11px', color: '#64748b' }}>#{ticket.id}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>

      {/* Create Request Modal */}
      {showCreateModal && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <h3>Raise Maintenance Request</h3>
            <form onSubmit={handleCreate} style={{ width: '100%', border: 'none', padding: '0', background: 'none' }}>
              <label>
                Select Asset *
                <select value={newRequest.assetId} onChange={(e) => setNewRequest({ ...newRequest, assetId: e.target.value })} required>
                  <option value="">Select Asset</option>
                  {assets.map((a) => (
                    <option key={a.id} value={a.id}>{a.assetTag} — {a.assetName}</option>
                  ))}
                </select>
              </label>
              <label>
                Priority *
                <select value={newRequest.priority} onChange={(e) => setNewRequest({ ...newRequest, priority: e.target.value })}>
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HIGH">High</option>
                  <option value="CRITICAL">Critical</option>
                </select>
              </label>
              <label>
                Description of Issue *
                <textarea
                  value={newRequest.description}
                  onChange={(e) => setNewRequest({ ...newRequest, description: e.target.value })}
                  rows="3"
                  required
                />
              </label>
              <div className="dialog-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Submit
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Details / Action Modal */}
      {selectedTicket && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <h3>Action Maintenance Request #{selectedTicket.id}</h3>
            <div style={{ marginBottom: '16px' }}>
              <strong>Asset Name:</strong> {selectedTicket.assetName} ({selectedTicket.assetTag})<br />
              <strong>Reported By:</strong> {selectedTicket.reportedByName}<br />
              <strong>Description:</strong> {selectedTicket.description}
            </div>

            <label>
              Technician Name
              <input type="text" value={techName} onChange={(e) => setTechName(e.target.value)} />
            </label>

            <label style={{ marginTop: '12px' }}>
              Notes
              <textarea value={notes} onChange={(e) => setNotes(e.target.value)} rows="3" />
            </label>

            <div className="dialog-actions" style={{ flexWrap: 'wrap', gap: '8px' }}>
              {selectedTicket.status === 'PENDING' && (
                <>
                  <button type="button" className="btn btn-danger" onClick={() => handleAction('REJECTED')}>
                    Reject
                  </button>
                  <button type="button" className="btn btn-primary" onClick={() => handleAction('APPROVED')}>
                    Approve Request
                  </button>
                </>
              )}
              {selectedTicket.status === 'APPROVED' && (
                <button type="button" className="btn btn-primary" onClick={() => handleAction('TECHNICIAN_ASSIGNED')}>
                  Assign Technician
                </button>
              )}
              {selectedTicket.status === 'TECHNICIAN_ASSIGNED' && (
                <button type="button" className="btn btn-primary" onClick={() => handleAction('IN_PROGRESS')}>
                  Start Repair
                </button>
              )}
              {selectedTicket.status === 'IN_PROGRESS' && (
                <button type="button" className="btn btn-primary" onClick={() => handleAction('RESOLVED')}>
                  Mark Resolved
                </button>
              )}

              <button type="button" className="btn btn-secondary" onClick={() => setSelectedTicket(null)}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
