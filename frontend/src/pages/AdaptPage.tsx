import { Button, Card, Form, Input, Modal, Select, Space, Typography, message } from 'antd';
import { Wand2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import { listContents } from '../api/contentApi';
import { adaptContent, listPlatformContents, updatePlatformContent } from '../api/platformContentApi';
import PlatformPreview from '../components/PlatformPreview';
import type { ContentItem, Platform, PlatformContent } from '../types';

const platforms: Platform[] = ['WECHAT', 'ZHIHU', 'BILIBILI', 'XIAOHONGSHU'];
const { TextArea } = Input;

export default function AdaptPage() {
  const [contents, setContents] = useState<ContentItem[]>([]);
  const [selectedContentId, setSelectedContentId] = useState<number>();
  const [items, setItems] = useState<PlatformContent[]>([]);
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState<PlatformContent | null>(null);
  const [form] = Form.useForm();

  async function loadContents() {
    const page = await listContents(0, 100);
    setContents(page.content);
    if (!selectedContentId && page.content[0]) {
      setSelectedContentId(page.content[0].id);
    }
  }

  async function loadAdapted(contentId: number) {
    const data = await listPlatformContents(contentId);
    setItems(data);
  }

  useEffect(() => {
    loadContents().catch((error) => message.error(error instanceof Error ? error.message : 'Failed to load content'));
  }, []);

  useEffect(() => {
    if (selectedContentId) {
      loadAdapted(selectedContentId).catch(() => setItems([]));
    }
  }, [selectedContentId]);

  async function generate() {
    if (!selectedContentId) {
      message.warning('Select content first');
      return;
    }
    setLoading(true);
    try {
      const data = await adaptContent(selectedContentId, platforms);
      setItems(data);
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to adapt content');
    } finally {
      setLoading(false);
    }
  }

  async function saveEdit(values: { title: string; summary?: string; body: string; tagsText: string }) {
    if (!editing) return;
    try {
      const updated = await updatePlatformContent(editing.id, {
        title: values.title,
        summary: values.summary,
        body: values.body,
        tags: values.tagsText.split(/[,，、\s]+/).map((tag) => tag.trim()).filter(Boolean)
      });
      setItems((current) => current.map((item) => (item.id === updated.id ? updated : item)));
      setEditing(null);
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to update platform content');
    }
  }

  return (
    <Space direction="vertical" size={16} className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3}>Platform adaptation</Typography.Title>
          <Typography.Text type="secondary">Generate and edit platform-specific versions before publishing.</Typography.Text>
        </div>
        <Space wrap>
          <Select
            className="content-select"
            placeholder="Select content"
            value={selectedContentId}
            onChange={setSelectedContentId}
            options={contents.map((item) => ({ value: item.id, label: `${item.title} · v${item.version}` }))}
          />
          <Button type="primary" icon={<Wand2 size={16} />} loading={loading} onClick={generate}>
            Generate
          </Button>
        </Space>
      </div>

      <div className="platform-grid">
        {items.map((item) => (
          <PlatformPreview key={item.id} item={item} onEdit={(target) => {
            setEditing(target);
            form.setFieldsValue({
              title: target.title,
              summary: target.summary,
              body: target.body,
              tagsText: target.tags.join(', ')
            });
          }} />
        ))}
      </div>
      {items.length === 0 && (
        <Card>
          <Typography.Text type="secondary">No platform content yet. Select a draft and generate platform versions.</Typography.Text>
        </Card>
      )}

      <Modal title="Edit platform content" open={!!editing} onCancel={() => setEditing(null)} footer={null} width={820}>
        <Form form={form} layout="vertical" onFinish={saveEdit}>
          <Form.Item name="title" label="Title" rules={[{ required: true }, { max: 120 }]}>
            <Input />
          </Form.Item>
          <Form.Item name="summary" label="Summary" rules={[{ max: 500 }]}>
            <TextArea rows={2} />
          </Form.Item>
          <Form.Item name="body" label="Body" rules={[{ required: true }]}>
            <TextArea rows={10} />
          </Form.Item>
          <Form.Item name="tagsText" label="Tags">
            <Input />
          </Form.Item>
          <div className="modal-actions">
            <Button onClick={() => setEditing(null)}>Cancel</Button>
            <Button type="primary" htmlType="submit">Save</Button>
          </div>
        </Form>
      </Modal>
    </Space>
  );
}
