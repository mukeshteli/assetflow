import { useState } from 'react';
import {
  useNotifications,
  useMarkNotificationRead,
  useMarkAllNotificationsRead
} from '../hooks/useOperations';

export default function NotificationsPage() {
  const [activeFilter, setActiveFilter] = useState('ALL');
  const { data: notifications = [], refetch } = useNotifications();

  const readMutation = useMarkNotificationRead();
  const readAllMutation = useMarkAllNotificationsRead();

  const handleMarkRead = (id) => {
    readMutation.mutate(id, {
      onSuccess: () => refetch()
    });
  };

  const handleMarkAllRead = () => {
    readAllMutation.mutate(null, {
      onSuccess: () => refetch()
    });
  };

  // Filter notifications based on type
  // Types: ALLOCATION, RETURN, TRANSFER_PENDING, TRANSFER_APPROVED, TRANSFER_REJECTED, BOOKING_CONFIRMED, BOOKING_CANCELLED, MAINTENANCE, AUDIT_ASSIGNED, AUDIT_DISCREPANCY
  const filteredNotifications = notifications.filter((n) => {
    if (activeFilter === 'ALL') return true;
    if (activeFilter === 'ALERTS') {
      return n.type === 'AUDIT_DISCREPANCY' || n.type === 'TRANSFER_REJECTED';
    }
    if (activeFilter === 'APPROVALS') {
      return n.type === 'TRANSFER_PENDING' || n.type === 'MAINTENANCE_APPROVED' || n.type === 'MAINTENANCE';
    }
    if (activeFilter === 'BOOKINGS') {
      return n.type.startsWith('BOOKING');
    }
    return true;
  });

  return (
    <div className="page">
      <div className="page-header">
        <h2>Notifications & Alerts</h2>
        {notifications.some((n) => !n.read) && (
          <button className="btn btn-secondary" onClick={handleMarkAllRead}>
            Mark All as Read
          </button>
        )}
      </div>

      {/* Tabs */}
      <div className="tabs-header">
        <button
          className={`tab-btn ${activeFilter === 'ALL' ? 'active' : ''}`}
          onClick={() => setActiveFilter('ALL')}
        >
          All
        </button>
        <button
          className={`tab-btn ${activeFilter === 'ALERTS' ? 'active' : ''}`}
          onClick={() => setActiveFilter('ALERTS')}
        >
          Alerts
        </button>
        <button
          className={`tab-btn ${activeFilter === 'APPROVALS' ? 'active' : ''}`}
          onClick={() => setActiveFilter('APPROVALS')}
        >
          Approvals
        </button>
        <button
          className={`tab-btn ${activeFilter === 'BOOKINGS' ? 'active' : ''}`}
          onClick={() => setActiveFilter('BOOKINGS')}
        >
          Bookings
        </button>
      </div>

      <div className="feed-list">
        {filteredNotifications.map((n) => (
          <div
            key={n.id}
            className="feed-item"
            style={{
              background: n.read ? '#15151a' : '#1e1b29',
              borderLeft: n.read ? '1px solid #22222a' : '4px solid #8b5cf6',
            }}
          >
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span className="badge badge-info" style={{ fontSize: '10px' }}>
                  {n.type}
                </span>
                {!n.read && <span style={{ width: '6px', height: '6px', borderRadius: '50%', background: '#8b5cf6' }} />}
              </div>
              <p style={{ margin: '8px 0 0 0', fontSize: '14px', color: '#cbd5e1' }}>{n.message}</p>
            </div>
            <div style={{ textAlign: 'right' }}>
              <div className="feed-meta">{new Date(n.createdAt).toLocaleString()}</div>
              {!n.read && (
                <button
                  type="button"
                  style={{
                    background: 'none',
                    border: 'none',
                    color: '#3b82f6',
                    cursor: 'pointer',
                    fontSize: '12px',
                    fontWeight: 'bold',
                    marginTop: '8px',
                    padding: 0,
                  }}
                  onClick={() => handleMarkRead(n.id)}
                >
                  Mark as Read
                </button>
              )}
            </div>
          </div>
        ))}
        {filteredNotifications.length === 0 && (
          <p style={{ color: '#94a3b8', textAlign: 'center' }}>No notifications found in this tab.</p>
        )}
      </div>
    </div>
  );
}
