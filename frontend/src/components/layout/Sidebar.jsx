import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getVisibleNavItems } from './navConfig';

export default function Sidebar() {
  const { role } = useAuth();
  const items = getVisibleNavItems(role);

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">AssetFlow</div>
      <nav>
        {items.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
