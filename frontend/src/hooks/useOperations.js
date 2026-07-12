import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../api/operationsApi';

export const useDashboardKpis = () => useQuery({
  queryKey: ['dashboard-kpis'],
  queryFn: api.getDashboardKpis,
  refetchInterval: 10000 // refresh every 10s
});

export const useAnalyticsStats = () => useQuery({
  queryKey: ['analytics-stats'],
  queryFn: api.getAnalyticsStats
});

export const useEmployees = () => useQuery({
  queryKey: ['employees'],
  queryFn: api.getEmployees
});

export const usePromoteEmployee = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, role }) => api.promoteEmployee(id, role),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] })
  });
};

export const useChangeEmployeeStatus = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }) => api.changeEmployeeStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] })
  });
};

export const useCreateDepartment = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createDepartment,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['departments'] })
  });
};

export const useUpdateDepartment = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, dept }) => api.updateDepartment(id, dept),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['departments'] })
  });
};

export const useChangeDepartmentStatus = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }) => api.changeDepartmentStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['departments'] })
  });
};

export const useCreateCategory = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createCategory,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['categories'] })
  });
};

export const useUpdateCategory = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, cat }) => api.updateCategory(id, cat),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['categories'] })
  });
};

export const useChangeCategoryStatus = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }) => api.changeCategoryStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['categories'] })
  });
};

export const useAllocateAsset = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.allocateAsset,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['assets'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useReturnAsset = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ assetId, returnNotes }) => api.returnAsset(assetId, returnNotes),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['assets'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useAssetHistory = (assetId) => useQuery({
  queryKey: ['asset-history', assetId],
  queryFn: () => api.getAssetHistory(assetId),
  enabled: !!assetId
});

export const useOverdueReturns = () => useQuery({
  queryKey: ['overdue-returns'],
  queryFn: api.getOverdueReturns
});

export const useRequestTransfer = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.requestTransfer,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['transfers'] })
  });
};

export const usePendingTransfers = () => useQuery({
  queryKey: ['pending-transfers'],
  queryFn: api.getPendingTransfers
});

export const useAllTransfers = () => useQuery({
  queryKey: ['transfers'],
  queryFn: api.getAllTransfers
});

export const useApproveTransfer = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.approveTransfer,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['transfers'] });
      qc.invalidateQueries({ queryKey: ['assets'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useRejectTransfer = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.rejectTransfer,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['transfers'] })
  });
};

export const useBookResource = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.bookResource,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookings'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useBookingsForAsset = (assetId) => useQuery({
  queryKey: ['bookings', assetId],
  queryFn: () => api.getBookingsForAsset(assetId),
  enabled: !!assetId
});

export const useActiveBookings = () => useQuery({
  queryKey: ['active-bookings'],
  queryFn: api.getActiveBookings
});

export const useAllBookings = () => useQuery({
  queryKey: ['bookings'],
  queryFn: api.getAllBookings
});

export const useCancelBooking = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.cancelBooking,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['bookings'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useCreateMaintenance = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createMaintenance,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['maintenance'] })
  });
};

export const useUpdateMaintenance = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, payload }) => api.updateMaintenance(id, payload),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['maintenance'] });
      qc.invalidateQueries({ queryKey: ['assets'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useAllMaintenance = () => useQuery({
  queryKey: ['maintenance'],
  queryFn: api.getAllMaintenance
});

export const useMaintenanceForAsset = (assetId) => useQuery({
  queryKey: ['maintenance', assetId],
  queryFn: () => api.getMaintenanceForAsset(assetId),
  enabled: !!assetId
});

export const useCreateAuditCycle = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.createAuditCycle,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['audit-cycles'] })
  });
};

export const useAllAuditCycles = () => useQuery({
  queryKey: ['audit-cycles'],
  queryFn: api.getAllAuditCycles
});

export const useAuditChecklist = (cycleId) => useQuery({
  queryKey: ['audit-checklist', cycleId],
  queryFn: () => api.getAuditChecklist(cycleId),
  enabled: !!cycleId
});

export const useSubmitAuditFinding = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ cycleId, finding }) => api.submitAuditFinding(cycleId, finding),
    onSuccess: (_, variables) => qc.invalidateQueries({ queryKey: ['audit-checklist', variables.cycleId] })
  });
};

export const useCloseAuditCycle = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.closeAuditCycle,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['audit-cycles'] });
      qc.invalidateQueries({ queryKey: ['assets'] });
      qc.invalidateQueries({ queryKey: ['dashboard-kpis'] });
    }
  });
};

export const useNotifications = () => useQuery({
  queryKey: ['notifications'],
  queryFn: api.getNotifications,
  refetchInterval: 10000
});

export const useMarkNotificationRead = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.markNotificationRead,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notifications'] })
  });
};

export const useMarkAllNotificationsRead = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.markAllNotificationsRead,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['notifications'] })
  });
};

export const useActivityLogs = () => useQuery({
  queryKey: ['activity-logs'],
  queryFn: api.getActivityLogs
});
