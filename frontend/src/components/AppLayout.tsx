import { Button, Layout, Menu, Typography } from 'antd';
import { FileText, LayoutDashboard, LogOut, PenTool, Rocket } from 'lucide-react';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { clearToken } from '../api/client';

const { Header, Sider, Content } = Layout;

export default function AppLayout() {
  const location = useLocation();
  const navigate = useNavigate();

  const selectedKey = location.pathname === '/' ? '/' : location.pathname;

  function logout() {
    clearToken();
    navigate('/login', { replace: true });
  }

  return (
    <Layout className="app-shell">
      <Sider width={232} className="app-sidebar">
        <div className="brand">
          <div className="brand-mark">M</div>
          <div>
            <Typography.Text className="brand-title">MultiPost</Typography.Text>
            <Typography.Text className="brand-subtitle">Creator publishing desk</Typography.Text>
          </div>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selectedKey]}
          className="side-menu"
          onClick={(item) => navigate(item.key)}
          items={[
            { key: '/', icon: <LayoutDashboard size={18} />, label: 'Workspace' },
            { key: '/contents', icon: <FileText size={18} />, label: 'Content' },
            { key: '/adapt', icon: <PenTool size={18} />, label: 'Adapt' },
            { key: '/publish', icon: <Rocket size={18} />, label: 'Publish' }
          ]}
        />
      </Sider>
      <Layout>
        <Header className="app-header">
          <div>
            <Typography.Title level={4} className="page-title">
              Multi-platform publishing workspace
            </Typography.Title>
            <Typography.Text type="secondary">Write once, adapt by platform, publish with task tracking.</Typography.Text>
          </div>
          <Button icon={<LogOut size={16} />} onClick={logout}>
            Sign out
          </Button>
        </Header>
        <Content className="app-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
