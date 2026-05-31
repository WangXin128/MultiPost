import { apiClient, unwrap } from './client';
import type { Platform, PublishBatch, PublishTask } from '../types';

export function createPublishBatch(payload: { contentId: number; requestId: string; platforms?: Platform[]; scheduledAt?: string }) {
  return unwrap<PublishBatch>(apiClient.post('/api/publish/batches', payload));
}

export function getPublishBatch(id: number) {
  return unwrap<PublishBatch>(apiClient.get(`/api/publish/batches/${id}`));
}

export function retryTask(id: number) {
  return unwrap<PublishTask>(apiClient.post(`/api/publish/tasks/${id}/retry`));
}
