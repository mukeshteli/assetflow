import { useAnalyticsStats } from '../hooks/useOperations';

export default function ReportsPage() {
  const { data: stats, isLoading } = useAnalyticsStats();

  const handleExportData = () => {
    if (!stats) return;
    const blob = new Blob([JSON.stringify(stats, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `assetflow_analytics_report_${new Date().toISOString().split('T')[0]}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  if (isLoading) {
    return <h2>Loading Reports & Analytics...</h2>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h2>Reports & Analytics</h2>
        <button className="btn btn-secondary" onClick={handleExportData} disabled={!stats}>
          📥 Export Report (JSON)
        </button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '32px' }}>
        {/* Utilization by Department */}
        <div style={{ background: '#15151a', border: '1px solid #22222a', borderRadius: '12px', padding: '24px' }}>
          <h3>Asset Utilization by Department</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '16px' }}>
            {stats?.departmentUtilizations?.map((dept) => (
              <div key={dept.departmentName}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '13px' }}>
                  <strong>{dept.departmentName}</strong>
                  <span>{dept.allocatedCount} / {dept.totalCount} assets ({dept.utilizationPercentage}%)</span>
                </div>
                <div style={{ width: '100%', height: '8px', background: '#0f0f12', borderRadius: '4px', overflow: 'hidden' }}>
                  <div style={{ width: `${dept.utilizationPercentage}%`, height: '100%', background: '#3b82f6', borderRadius: '4px' }} />
                </div>
              </div>
            ))}
            {(!stats?.departmentUtilizations || stats.departmentUtilizations.length === 0) && (
              <p style={{ color: '#94a3b8' }}>No department statistics found.</p>
            )}
          </div>
        </div>

        {/* Maintenance frequency by Category */}
        <div style={{ background: '#15151a', border: '1px solid #22222a', borderRadius: '12px', padding: '24px' }}>
          <h3>Maintenance Count by Category</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', marginTop: '16px' }}>
            {stats?.categoryMaintenances?.map((cat) => (
              <div key={cat.categoryName}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '6px', fontSize: '13px' }}>
                  <strong>{cat.categoryName}</strong>
                  <span>{cat.maintenanceCount} ticket(s)</span>
                </div>
                <div style={{ width: '100%', height: '8px', background: '#0f0f12', borderRadius: '4px', overflow: 'hidden' }}>
                  <div style={{ width: `${Math.min(cat.maintenanceCount * 15, 100)}%`, height: '100%', background: '#ef4444', borderRadius: '4px' }} />
                </div>
              </div>
            ))}
            {(!stats?.categoryMaintenances || stats.categoryMaintenances.length === 0) && (
              <p style={{ color: '#94a3b8' }}>No maintenance data reported.</p>
            )}
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '32px', marginTop: '32px' }}>
        {/* Most-used vs Idle assets */}
        <div style={{ background: '#15151a', border: '1px solid #22222a', borderRadius: '12px', padding: '24px' }}>
          <h3>Asset Popularity</h3>
          <h4 style={{ marginTop: '0', color: '#3b82f6' }}>Most-Used Assets</h4>
          <div className="table-container" style={{ margin: '8px 0 24px 0' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Asset</th>
                  <th>Total Usages</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {stats?.mostUsedAssets?.map((item) => (
                  <tr key={item.assetTag}>
                    <td><strong>{item.assetTag}</strong> — {item.assetName}</td>
                    <td>{item.usageCount}</td>
                    <td><span className="badge badge-info">{item.status}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <h4 style={{ marginTop: '0', color: '#10b981' }}>Idle Assets (Unused & Available)</h4>
          <div className="table-container" style={{ margin: '8px 0 0 0' }}>
            <table className="table">
              <thead>
                <tr>
                  <th>Asset</th>
                  <th>Usages</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {stats?.idleAssets?.map((item) => (
                  <tr key={item.assetTag}>
                    <td><strong>{item.assetTag}</strong> — {item.assetName}</td>
                    <td>{item.usageCount}</td>
                    <td><span className="badge badge-success">{item.status}</span></td>
                  </tr>
                ))}
                {(!stats?.idleAssets || stats.idleAssets.length === 0) && (
                  <tr>
                    <td colSpan="3" style={{ textAlign: 'center', color: '#94a3b8' }}>No idle assets in inventory.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>

        {/* Assets nearing retirement / Heatmap */}
        <div>
          <div style={{ background: '#15151a', border: '1px solid #22222a', borderRadius: '12px', padding: '24px', marginBottom: '32px' }}>
            <h3>Assets Nearing Retirement</h3>
            <div className="table-container" style={{ margin: '8px 0 0 0' }}>
              <table className="table">
                <thead>
                  <tr>
                    <th>Asset</th>
                    <th>Condition</th>
                    <th>Warranty Expiry</th>
                  </tr>
                </thead>
                <tbody>
                  {stats?.nearRetirementAssets?.map((item) => (
                    <tr key={item.assetTag}>
                      <td><strong>{item.assetTag}</strong> — {item.assetName}</td>
                      <td>
                        <span className={`badge ${item.condition === 'POOR' || item.condition === 'DAMAGED' ? 'badge-danger' : 'badge-warning'}`}>
                          {item.condition}
                        </span>
                      </td>
                      <td>{item.warrantyExpiry ?? 'Expired / None'}</td>
                    </tr>
                  ))}
                  {(!stats?.nearRetirementAssets || stats.nearRetirementAssets.length === 0) && (
                    <tr>
                      <td colSpan="3" style={{ textAlign: 'center', color: '#94a3b8' }}>No assets flagged for retirement.</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div style={{ background: '#15151a', border: '1px solid #22222a', borderRadius: '12px', padding: '24px' }}>
            <h3>Resource Booking Peak Windows</h3>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '12px', marginTop: '16px' }}>
              {stats?.bookingHeatmap?.map((item) => (
                <div key={item.hourSlot} style={{ background: '#0f0f12', border: '1px solid #22222a', borderRadius: '8px', padding: '16px', textAlign: 'center' }}>
                  <div style={{ fontSize: '12px', color: '#94a3b8', marginBottom: '4px' }}>{item.hourSlot}</div>
                  <div style={{ fontSize: '24px', fontWeight: 'bold', color: item.bookingCount > 0 ? '#3b82f6' : '#fff' }}>
                    {item.bookingCount}
                  </div>
                  <div style={{ fontSize: '11px', color: '#64748b', marginTop: '4px' }}>Bookings</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
