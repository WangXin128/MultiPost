import { apiClient, unwrap } from './client';
import type { ContentItem, ContentStatus, ContentType, PageResponse } from '../types';

export interface ContentPayload {
  title: string;
  summary?: string;
  body: string;
  tags?: string;
  coverUrl?: string;
  contentType: ContentType;
  status?: ContentStatus;
}

export function listContents(page = 0, size = 20) {
  return unwrap<PageResponse<ContentItem>>(apiClient.get('/api/contents', { params: { page, size, sort: 'updatedAt,desc' } }));
}

export function createContent(payload: ContentPayload) {
  return unwrap<ContentItem>(apiClient.post('/api/contents', payload));
}

export function updateContent(id: number, payload: Required<Pick<ContentPayload, 'title' | 'body' | 'contentType'>> & ContentPayload & { status: ContentStatus }) {
  return unwrap<ContentItem>(apiClient.put(`/api/contents/${id}`, payload));
}

export function deleteContent(id: number) {
  return unwrap<void>(apiClient.delete(`/api/contents/${id}`));
}
