import { useState } from 'react';
import { useDepartments } from '../hooks/useDepartments';
import { useAssetCategories } from '../hooks/useAssetCategories';
import {
  useEmployees,
  usePromoteEmployee,
  useChangeEmployeeStatus,
  useCreateDepartment,
  useUpdateDepartment,
  useChangeDepartmentStatus,
  useCreateCategory,
  useUpdateCategory,
  useChangeCategoryStatus
} from '../hooks/useOperations';

export default function OrganizationSetupPage() {
  const [activeTab, setActiveTab] = useState('departments');

  // Queries
  const { data: departments = [], refetch: refetchDepts } = useDepartments();
  const { data: categories = [], refetch: refetchCats } = useAssetCategories();
  const { data: employees = [], refetch: refetchEmps } = useEmployees();

  // Mutations
  const createDeptMutation = useCreateDepartment();
  const updateDeptMutation = useUpdateDepartment();
  const changeDeptStatusMutation = useChangeDepartmentStatus();

  const createCatMutation = useCreateCategory();
  const updateCatMutation = useUpdateCategory();
  const changeCatStatusMutation = useChangeCategoryStatus();

  const promoteEmpMutation = usePromoteEmployee();
  const changeEmpStatusMutation = useChangeEmployeeStatus();

  // Modal State
  const [showDeptModal, setShowDeptModal] = useState(false);
  const [editingDept, setEditingDept] = useState(null);
  const [deptForm, setDeptForm] = useState({ name: '', parentDepartmentId: '', headEmployeeId: '' });

  const [showCatModal, setShowCatModal] = useState(false);
  const [editingCat, setEditingCat] = useState(null);
  const [catForm, setCatForm] = useState({ name: '', description: '' });

  // Handlers - Departments
  const handleOpenDeptModal = (dept = null) => {
    if (dept) {
      setEditingDept(dept);
      setDeptForm({
        name: dept.name,
        parentDepartmentId: dept.parentDepartmentId ?? '',
        headEmployeeId: dept.headEmployeeId ?? '',
      });
    } else {
      setEditingDept(null);
      setDeptForm({ name: '', parentDepartmentId: '', headEmployeeId: '' });
    }
    setShowDeptModal(true);
  };

  const handleSaveDept = (e) => {
    e.preventDefault();
    const payload = {
      name: deptForm.name,
      parentDepartmentId: deptForm.parentDepartmentId ? Number(deptForm.parentDepartmentId) : null,
      headEmployeeId: deptForm.headEmployeeId ? Number(deptForm.headEmployeeId) : null
    };

    if (editingDept) {
      updateDeptMutation.mutate({ id: editingDept.id, dept: payload }, {
        onSuccess: () => {
          setShowDeptModal(false);
          refetchDepts();
        }
      });
    } else {
      createDeptMutation.mutate(payload, {
        onSuccess: () => {
          setShowDeptModal(false);
          refetchDepts();
        }
      });
    }
  };

  const toggleDeptStatus = (id, currentStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    changeDeptStatusMutation.mutate({ id, status: nextStatus }, {
      onSuccess: () => refetchDepts()
    });
  };

  // Handlers - Categories
  const handleOpenCatModal = (cat = null) => {
    if (cat) {
      setEditingCat(cat);
      setCatForm({ name: cat.name, description: cat.description ?? '' });
    } else {
      setEditingCat(null);
      setCatForm({ name: '', description: '' });
    }
    setShowCatModal(true);
  };

  const handleSaveCat = (e) => {
    e.preventDefault();
    if (editingCat) {
      updateCatMutation.mutate({ id: editingCat.id, cat: catForm }, {
        onSuccess: () => {
          setShowCatModal(false);
          refetchCats();
        }
      });
    } else {
      createCatMutation.mutate(catForm, {
        onSuccess: () => {
          setShowCatModal(false);
          refetchCats();
        }
      });
    }
  };

  const toggleCatStatus = (id, currentStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    changeCatStatusMutation.mutate({ id, status: nextStatus }, {
      onSuccess: () => refetchCats()
    });
  };

  // Handlers - Employees
  const handlePromoteRole = (id, newRole) => {
    promoteEmpMutation.mutate({ id, role: newRole }, {
      onSuccess: () => refetchEmps()
    });
  };

  const toggleEmpStatus = (id, currentStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    changeEmpStatusMutation.mutate({ id, status: nextStatus }, {
      onSuccess: () => refetchEmps()
    });
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Organization Setup</h2>
        {activeTab === 'departments' && (
          <button className="btn btn-primary" onClick={() => handleOpenDeptModal()}>
            + Add Department
          </button>
        )}
        {activeTab === 'categories' && (
          <button className="btn btn-primary" onClick={() => handleOpenCatModal()}>
            + Add Category
          </button>
        )}
      </div>

      <div className="design-note">
        <strong>Designer's Note:</strong> Editing a department or category here drives the picklist filters in the Asset Directory, Allocations, and Resource Bookings screens. All master data is fetched dynamically.
      </div>

      {/* Tabs */}
      <div className="tabs-header">
        <button
          className={`tab-btn ${activeTab === 'departments' ? 'active' : ''}`}
          onClick={() => setActiveTab('departments')}
        >
          Departments
        </button>
        <button
          className={`tab-btn ${activeTab === 'categories' ? 'active' : ''}`}
          onClick={() => setActiveTab('categories')}
        >
          Categories
        </button>
        <button
          className={`tab-btn ${activeTab === 'employees' ? 'active' : ''}`}
          onClick={() => setActiveTab('employees')}
        >
          Employee Directory
        </button>
      </div>

      {/* Tab Contents */}
      {activeTab === 'departments' && (
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Department</th>
                <th>Head</th>
                <th>Parent Dept</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {departments.map((dept) => (
                <tr key={dept.id}>
                  <td><strong>{dept.name}</strong></td>
                  <td>{dept.headEmployeeName ?? 'No assigned head'}</td>
                  <td>{dept.parentDepartmentName ?? 'None (Root)'}</td>
                  <td>
                    <span className={`badge ${dept.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {dept.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-secondary" style={{ padding: '6px 12px', marginRight: '8px' }} onClick={() => handleOpenDeptModal(dept)}>
                      Edit
                    </button>
                    <button className="btn btn-danger" style={{ padding: '6px 12px' }} onClick={() => toggleDeptStatus(dept.id, dept.status)}>
                      Toggle Status
                    </button>
                  </td>
                </tr>
              ))}
              {departments.length === 0 && (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center' }}>No departments found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'categories' && (
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Category Name</th>
                <th>Description</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((cat) => (
                <tr key={cat.id}>
                  <td><strong>{cat.name}</strong></td>
                  <td>{cat.description}</td>
                  <td>
                    <span className={`badge ${cat.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {cat.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-secondary" style={{ padding: '6px 12px', marginRight: '8px' }} onClick={() => handleOpenCatModal(cat)}>
                      Edit
                    </button>
                    <button className="btn btn-danger" style={{ padding: '6px 12px' }} onClick={() => toggleCatStatus(cat.id, cat.status)}>
                      Toggle Status
                    </button>
                  </td>
                </tr>
              ))}
              {categories.length === 0 && (
                <tr>
                  <td colSpan="4" style={{ textAlign: 'center' }}>No categories found.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {activeTab === 'employees' && (
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Department</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {employees.map((emp) => (
                <tr key={emp.id}>
                  <td><strong>{emp.fullName}</strong></td>
                  <td>{emp.email}</td>
                  <td>{emp.departmentName ?? 'Unassigned'}</td>
                  <td>
                    <span className="badge badge-info">{emp.role}</span>
                  </td>
                  <td>
                    <span className={`badge ${emp.status === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                      {emp.status}
                    </span>
                  </td>
                  <td>
                    <select
                      value={emp.role}
                      onChange={(e) => handlePromoteRole(emp.id, e.target.value)}
                      style={{ padding: '6px', fontSize: '12px', marginRight: '8px' }}
                    >
                      <option value="EMPLOYEE">Employee</option>
                      <option value="DEPARTMENT_HEAD">Department Head</option>
                      <option value="ASSET_MANAGER">Asset Manager</option>
                      <option value="ADMIN">Admin</option>
                    </select>
                    <button className="btn btn-secondary" style={{ padding: '6px 12px' }} onClick={() => toggleEmpStatus(emp.id, emp.status)}>
                      Toggle Status
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Department Modal */}
      {showDeptModal && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <h3>{editingDept ? 'Edit Department' : 'Create Department'}</h3>
            <form onSubmit={handleSaveDept} style={{ width: '100%', border: 'none', padding: '0', background: 'none' }}>
              <label>
                Department Name *
                <input
                  type="text"
                  value={deptForm.name}
                  onChange={(e) => setDeptForm({ ...deptForm, name: e.target.value })}
                  required
                />
              </label>
              <label>
                Parent Department
                <select
                  value={deptForm.parentDepartmentId}
                  onChange={(e) => setDeptForm({ ...deptForm, parentDepartmentId: e.target.value })}
                >
                  <option value="">None (Root)</option>
                  {departments
                    .filter((d) => !editingDept || d.id !== editingDept.id)
                    .map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                </select>
              </label>
              <label>
                Department Head
                <select
                  value={deptForm.headEmployeeId}
                  onChange={(e) => setDeptForm({ ...deptForm, headEmployeeId: e.target.value })}
                >
                  <option value="">Unassigned</option>
                  {employees.map((e) => (
                    <option key={e.id} value={e.id}>{e.fullName}</option>
                  ))}
                </select>
              </label>
              <div className="dialog-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowDeptModal(false)}>
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

      {/* Category Modal */}
      {showCatModal && (
        <div className="dialog-overlay">
          <div className="dialog-content">
            <h3>{editingCat ? 'Edit Category' : 'Create Category'}</h3>
            <form onSubmit={handleSaveCat} style={{ width: '100%', border: 'none', padding: '0', background: 'none' }}>
              <label>
                Category Name *
                <input
                  type="text"
                  value={catForm.name}
                  onChange={(e) => setCatForm({ ...catForm, name: e.target.value })}
                  required
                />
              </label>
              <label>
                Description
                <textarea
                  value={catForm.description}
                  onChange={(e) => setCatForm({ ...catForm, description: e.target.value })}
                  rows="3"
                />
              </label>
              <div className="dialog-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowCatModal(false)}>
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
