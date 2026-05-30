import { Button, Form, Input, Modal, Select } from 'antd';
import type { ContentItem, ContentStatus, ContentType } from '../types';
import type { ContentPayload } from '../api/contentApi';

const { TextArea } = Input;

interface Props {
  open: boolean;
  initial?: ContentItem | null;
  saving: boolean;
  onCancel: () => void;
  onSubmit: (payload: ContentPayload & { status?: ContentStatus }) => void;
}

export default function ContentEditor({ open, initial, saving, onCancel, onSubmit }: Props) {
  const [form] = Form.useForm();

  return (
    <Modal
      title={initial ? 'Edit content' : 'Create content'}
      open={open}
      onCancel={onCancel}
      width={860}
      footer={null}
      destroyOnClose
      afterOpenChange={(visible) => {
        if (visible) {
          form.setFieldsValue({
            title: initial?.title || '',
            summary: initial?.summary || '',
            body: initial?.body || '',
            tags: initial?.tags || '',
            coverUrl: initial?.coverUrl || '',
            contentType: initial?.contentType || 'ARTICLE',
            status: initial?.status || 'DRAFT'
          });
        }
      }}
    >
      <Form form={form} layout="vertical" onFinish={(values: ContentPayload & { status: ContentStatus }) => onSubmit(values)}>
        <Form.Item name="title" label="Title" rules={[{ required: true }, { max: 120 }]}>
          <Input placeholder="A clear title for the original content" />
        </Form.Item>
        <Form.Item name="summary" label="Summary" rules={[{ max: 500 }]}>
          <TextArea rows={2} placeholder="Short summary used by platform adapters" />
        </Form.Item>
        <Form.Item name="body" label="Body" rules={[{ required: true }]}>
          <TextArea rows={10} placeholder="Write the original article, note, or script here" />
        </Form.Item>
        <div className="form-grid">
          <Form.Item name="tags" label="Tags" rules={[{ max: 500 }]}>
            <Input placeholder="AI, writing, productivity" />
          </Form.Item>
          <Form.Item name="coverUrl" label="Cover URL" rules={[{ max: 500 }]}>
            <Input placeholder="https://..." />
          </Form.Item>
          <Form.Item name="contentType" label="Type" rules={[{ required: true }]}>
            <Select<ContentType>
              options={[
                { value: 'ARTICLE', label: 'Article' },
                { value: 'VIDEO_SCRIPT', label: 'Video script' },
                { value: 'NOTE', label: 'Note' }
              ]}
            />
          </Form.Item>
          <Form.Item name="status" label="Status">
            <Select<ContentStatus>
              options={[
                { value: 'DRAFT', label: 'Draft' },
                { value: 'READY', label: 'Ready' },
                { value: 'ARCHIVED', label: 'Archived' }
              ]}
            />
          </Form.Item>
        </div>
        <div className="modal-actions">
          <Button onClick={onCancel}>Cancel</Button>
          <Button type="primary" htmlType="submit" loading={saving}>
            Save
          </Button>
        </div>
      </Form>
    </Modal>
  );
}
