import { Button, Card, Space, Tag, Typography } from 'antd';
import { Edit3 } from 'lucide-react';
import type { PlatformContent } from '../types';

const platformLabel: Record<string, string> = {
  WECHAT: 'WeChat',
  ZHIHU: 'Zhihu',
  BILIBILI: 'Bilibili',
  XIAOHONGSHU: 'Xiaohongshu'
};

export default function PlatformPreview({ item, onEdit }: { item: PlatformContent; onEdit: (item: PlatformContent) => void }) {
  return (
    <Card
      title={
        <Space>
          <span>{platformLabel[item.platform]}</span>
          <Tag>v{item.sourceVersion}</Tag>
        </Space>
      }
      extra={
        <Button size="small" icon={<Edit3 size={14} />} onClick={() => onEdit(item)}>
          Edit
        </Button>
      }
      className="platform-card"
    >
      <Typography.Title level={5}>{item.title}</Typography.Title>
      {item.summary && <Typography.Paragraph type="secondary">{item.summary}</Typography.Paragraph>}
      <Typography.Paragraph className="preview-body">{item.body}</Typography.Paragraph>
      <Space size={[4, 6]} wrap>
        {item.tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </Space>
    </Card>
  );
}
