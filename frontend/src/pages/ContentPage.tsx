import { Button, Card, Popconfirm, Space, Table, Tag, Typography, message } from 'antd';
import { Edit3, Plus, Trash2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import { createContent, deleteContent, listContents, updateContent } from '../api/contentApi';
import ContentEditor from '../components/ContentEditor';
import type { ContentItem, ContentStatus } from '../types';
import type { ContentPayload } from '../api/contentApi';

export default function ContentPage() {
  const [contents, setContents] = useState<ContentItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [editorOpen, setEditorOpen] = useState(false);
  const [editing, setEditing] = useState<ContentItem | null>(null);

  async function load() {
    setLoading(true);
    try {
      const page = await listContents(0, 50);
      setContents(page.content);
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to load content');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function save(payload: ContentPayload & { status?: ContentStatus }) {
    setSaving(true);
    try {
      if (editing) {
        await updateContent(editing.id, { ...payload, status: payload.status || 'DRAFT' });
      } else {
        await createContent(payload);
      }
      setEditorOpen(false);
      setEditing(null);
      await load();
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to save content');
    } finally {
      setSaving(false);
    }
  }

  async function remove(id: number) {
    try {
      await deleteContent(id);
      await load();
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to delete content');
    }
  }

  return (
    <Space direction="vertical" size={16} className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3}>Content</Typography.Title>
          <Typography.Text type="secondary">Create and manage the source drafts used by platform adapters.</Typography.Text>
        </div>
        <Button type="primary" icon={<Plus size={16} />} onClick={() => { setEditing(null); setEditorOpen(true); }}>
          New content
        </Button>
      </div>

      <Card>
        <Table
          rowKey="id"
          loading={loading}
          dataSource={contents}
          columns={[
            {
              title: 'Title',
              dataIndex: 'title',
              render: (title: string, item) => (
                <div>
                  <Typography.Text strong>{title}</Typography.Text>
                  {item.summary && <Typography.Paragraph type="secondary" className="table-summary">{item.summary}</Typography.Paragraph>}
                </div>
              )
            },
            { title: 'Type', dataIndex: 'contentType', width: 140 },
            {
              title: 'Status',
              dataIndex: 'status',
              width: 120,
              render: (status: string) => <Tag color={status === 'READY' ? 'green' : 'default'}>{status}</Tag>
            },
            { title: 'Version', dataIndex: 'version', width: 90 },
            {
              title: 'Actions',
              width: 170,
              render: (_, item) => (
                <Space>
                  <Button size="small" icon={<Edit3 size={14} />} onClick={() => { setEditing(item); setEditorOpen(true); }}>
                    Edit
                  </Button>
                  <Popconfirm title="Delete this content?" onConfirm={() => remove(item.id)}>
                    <Button size="small" danger icon={<Trash2 size={14} />} />
                  </Popconfirm>
                </Space>
              )
            }
          ]}
        />
      </Card>

      <ContentEditor
        open={editorOpen}
        initial={editing}
        saving={saving}
        onCancel={() => { setEditorOpen(false); setEditing(null); }}
        onSubmit={save}
      />
    </Space>
  );
}
