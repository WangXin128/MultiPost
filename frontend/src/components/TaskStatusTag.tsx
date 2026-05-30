import { Tag } from 'antd';
import type { PublishBatchStatus, PublishTaskStatus } from '../types';

const statusColor: Record<string, string> = {
  PENDING: 'default',
  PUBLISHING: 'processing',
  RETRYING: 'warning',
  SUCCESS: 'success',
  FAILED: 'error',
  UNKNOWN: 'default',
  ALL_SUCCESS: 'success',
  PARTIAL_SUCCESS: 'warning',
  ALL_FAILED: 'error'
};

export default function TaskStatusTag({ status }: { status: PublishTaskStatus | PublishBatchStatus }) {
  return <Tag color={statusColor[status] || 'default'}>{status}</Tag>;
}
