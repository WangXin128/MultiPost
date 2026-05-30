import { Button, Card, Col, Row, Space, Statistic, Table, Typography, message } from 'antd';
import { FilePlus2, PenTool, Rocket } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { listContents } from '../api/contentApi';
import type { ContentItem } from '../types';

export default function DashboardPage() {
  const [contents, setContents] = useState<ContentItem[]>([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function load() {
    setLoading(true);
    try {
      const page = await listContents(0, 6);
      setContents(page.content);
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  return (
    <Space direction="vertical" size={18} className="page-stack">
      <section className="workspace-hero">
        <div>
          <Typography.Title level={2}>Creator publishing desk</Typography.Title>
          <Typography.Paragraph>
            Manage drafts, generate platform-specific versions, and track publishing tasks from one backend-driven workflow.
          </Typography.Paragraph>
        </div>
        <Space wrap>
          <Button type="primary" icon={<FilePlus2 size={16} />} onClick={() => navigate('/contents')}>
            New content
          </Button>
          <Button icon={<PenTool size={16} />} onClick={() => navigate('/adapt')}>
            Adapt
          </Button>
          <Button icon={<Rocket size={16} />} onClick={() => navigate('/publish')}>
            Publish
          </Button>
        </Space>
      </section>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="Recent drafts" value={contents.length} />
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="Supported platforms" value={4} />
          </Card>
        </Col>
        <Col xs={24} md={8}>
          <Card>
            <Statistic title="Publishing mode" value="Mock" />
          </Card>
        </Col>
      </Row>

      <Card title="Recent content">
        <Table
          rowKey="id"
          loading={loading}
          dataSource={contents}
          pagination={false}
          columns={[
            { title: 'Title', dataIndex: 'title' },
            { title: 'Type', dataIndex: 'contentType', width: 150 },
            { title: 'Status', dataIndex: 'status', width: 120 },
            { title: 'Version', dataIndex: 'version', width: 100 }
          ]}
        />
      </Card>
    </Space>
  );
}
