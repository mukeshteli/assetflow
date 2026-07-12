// Central nav definition so adding a screen never means hunting
// through JSX in multiple places. roles: undefined = visible to everyone.
export const navItems = [
  { label: 'Dashboard', path: '/dashboard' },
  { label: 'Organization setup', path: '/organization-setup', roles: ['ADMIN'] },
  { label: 'Assets', path: '/assets' },
  { label: 'Allocation & Transfer', path: '/allocations' },
  { label: 'Resource Booking', path: '/bookings' },
  { label: 'Maintenance', path: '/maintenance' },
  { label: 'Audit', path: '/audits', roles: ['ADMIN', 'ASSET_MANAGER'] },
  { label: 'Reports', path: '/reports', roles: ['ADMIN', 'ASSET_MANAGER', 'DEPARTMENT_HEAD'] },
  { label: 'Notifications', path: '/notifications' },
];

export function getVisibleNavItems(role) {
  return navItems.filter((item) => !item.roles || item.roles.includes(role));
}
