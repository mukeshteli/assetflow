import { Link } from 'react-router-dom';
import { useDashboardKpis, useActivityLogs, useOverdueReturns } from '../hooks/useOperations';

export default function DashboardPage() {
  const { data: kpis, isLoading: kpisLoading } = useDashboardKpis();
  const { data: logs = [], isLoading: logsLoading } = useActivityLogs();
  const { data: overdue = [], isLoading: overdueLoading } = useOverdueReturns();

  if (kpisLoading || logsLoading || overdueLoading) {
    return <h2>Loading Dashboard...</h2>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Dashboard</h2>
      </div>

      {overdue.length > 0 && (
        <div className="callout-banner">
          <div>
            <p><strong>Overdue Return Alert:</strong> {overdue.length} asset(s) are past their expected return date.</p>
          </div>
          <Link to="/allocations" className="btn btn-secondary" style={{ padding: '6px 12px', fontSize: '12px' }}>
            View Details
          </Link>
        </div>
      )}

      {/* KPI Card Grid */}
      <div className="kpi-grid">
        <div className="kpi-card">
          <div className="kpi-title">Available Assets</div>
          <div className="kpi-val">{kpis?.availableAssets ?? 0}</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-title">Allocated Assets</div>
          <div className="kpi-val">{kpis?.allocatedAssets ?? 0}</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-title">Active Bookings</div>
          <div className="kpi-val">{kpis?.activeBookings ?? 0}</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-title">Pending Transfers</div>
          <div className="kpi-val">{kpis?.pendingTransfers ?? 0}</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-title">Maintenance Pending</div>
          <div className="kpi-val">{kpis?.maintenanceCount ?? 0}</div>
        </div>
        <div className="kpi-card">
          <div className="kpi-title">Overdue Returns</div>
          <div className="kpi-val" style={{ color: overdue.length > 0 ? '#ef4444' : '#fff' }}>
            {kpis?.overdueCount ?? 0}
          </div>
        </div>
      </div>

      {/* Quick Actions & Recent Activity layout */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '32px', marginTop: '32px' }}>
        <div>
          <h3>Quick Actions</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <Link to="/assets/new" className="btn btn-primary" style={{ width: '100%' }}>
              Register Asset
            </Link>
            <Link to="/bookings" className="btn btn-secondary" style={{ width: '100%' }}>
              Book Resource
            </Link>
            <Link to="/allocations" className="btn btn-secondary" style={{ width: '100%' }}>
              Raise Allocation
            </Link>
          </div>
        </div>

        <div>
          <h3>Recent Activity Logs</h3>
          <div className="feed-list">
            {logs.slice(0, 5).map((log) => (
              <div key={log.id} className="feed-item">
                <div>
                  <strong>{log.action}</strong>
                  <div style={{ fontSize: '13px', color: '#94a3b8', marginTop: '4px' }}>{log.details}</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '13px', color: '#cbd5e1' }}>{log.employeeName}</div>
                  <div className="feed-meta">{new Date(log.createdAt).toLocaleString()}</div>
                </div>
              </div>
            ))}
            {logs.length === 0 && <p style={{ color: '#94a3b8' }}>No recent activity.</p>}
          </div>
        </div>
      </div>
    </div>
  );
}
