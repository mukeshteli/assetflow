import axiosClient from './axiosClient';

// Employees & Org Setup
export const getEmployees = () => axiosClient.get('/employees').then(res => res.data);
export const promoteEmployee = (id, role) => axiosClient.patch(`/employees/${id}/role?role=${role}`).then(res => res.data);
export const changeEmployeeStatus = (id, status) => axiosClient.patch(`/employees/${id}/status?status=${status}`).then(res => res.data);

export const createDepartment = (dept) => axiosClient.post('/departments', dept).then(res => res.data);
export const updateDepartment = (id, dept) => axiosClient.put(`/departments/${id}`, dept).then(res => res.data);
export const changeDepartmentStatus = (id, status) => axiosClient.patch(`/departments/${id}/status?status=${status}`).then(res => res.data);

export const createCategory = (cat) => axiosClient.post('/categories', cat).then(res => res.data);
export const updateCategory = (id, cat) => axiosClient.put(`/categories/${id}`, cat).then(res => res.data);
export const changeCategoryStatus = (id, status) => axiosClient.patch(`/categories/${id}/status?status=${status}`).then(res => res.data);

// Allocations & Transfers
export const allocateAsset = (alloc) => axiosClient.post('/allocations', alloc).then(res => res.data);
export const returnAsset = (assetId, returnNotes) => axiosClient.post(`/allocations/${assetId}/return`, { returnNotes }).then(res => res.data);
export const getAssetHistory = (assetId) => axiosClient.get(`/allocations/history/${assetId}`).then(res => res.data);
export const getOverdueReturns = () => axiosClient.get('/allocations/overdue').then(res => res.data);

export const requestTransfer = (transfer) => axiosClient.post('/allocations/transfers', transfer).then(res => res.data);
export const getPendingTransfers = () => axiosClient.get('/allocations/transfers/pending').then(res => res.data);
export const getAllTransfers = () => axiosClient.get('/allocations/transfers').then(res => res.data);
export const approveTransfer = (id) => axiosClient.post(`/allocations/transfers/${id}/approve`).then(res => res.data);
export const rejectTransfer = (id) => axiosClient.post(`/allocations/transfers/${id}/reject`).then(res => res.data);

// Bookings
export const bookResource = (booking) => axiosClient.post('/bookings', booking).then(res => res.data);
export const getBookingsForAsset = (assetId) => axiosClient.get(`/bookings/asset/${assetId}`).then(res => res.data);
export const getActiveBookings = () => axiosClient.get('/bookings/active').then(res => res.data);
export const getAllBookings = () => axiosClient.get('/bookings').then(res => res.data);
export const cancelBooking = (id) => axiosClient.post(`/bookings/${id}/cancel`).then(res => res.data);

// Maintenance
export const createMaintenance = (req) => axiosClient.post('/maintenance', req).then(res => res.data);
export const updateMaintenance = (id, payload) => axiosClient.put(`/maintenance/${id}`, payload).then(res => res.data);
export const getAllMaintenance = () => axiosClient.get('/maintenance').then(res => res.data);
export const getMaintenanceForAsset = (assetId) => axiosClient.get(`/maintenance/asset/${assetId}`).then(res => res.data);

// Audits
export const createAuditCycle = (cycle) => axiosClient.post('/audits', cycle).then(res => res.data);
export const getAllAuditCycles = () => axiosClient.get('/audits').then(res => res.data);
export const getAuditChecklist = (cycleId) => axiosClient.get(`/audits/${cycleId}/checklist`).then(res => res.data);
export const submitAuditFinding = (cycleId, finding) => axiosClient.post(`/audits/${cycleId}/findings`, finding).then(res => res.data);
export const closeAuditCycle = (cycleId) => axiosClient.post(`/audits/${cycleId}/close`).then(res => res.data);

// Reports & Analytics
export const getDashboardKpis = () => axiosClient.get('/reports/dashboard-kpis').then(res => res.data);
export const getAnalyticsStats = () => axiosClient.get('/reports/analytics').then(res => res.data);

// Notifications & Activity Logs
export const getNotifications = () => axiosClient.get('/notifications').then(res => res.data);
export const markNotificationRead = (id) => axiosClient.post(`/notifications/${id}/read`).then(res => res.data);
export const markAllNotificationsRead = () => axiosClient.post('/notifications/read-all').then(res => res.data);
export const getActivityLogs = () => axiosClient.get('/activity-logs').then(res => res.data);
