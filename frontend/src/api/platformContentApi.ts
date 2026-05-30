import { apiClient, unwrap } from './client';
import type { Platform, PlatformContent } from '../types';

export function adaptContent(contentId: number, platforms?: Platform[]) {
  const payload = platforms && platforms.length > 0 ? { platforms } : {};
  return unwrap<PlatformContent[]>(apiClient.post(`/api/contents/${contentId}/adapt`, payload));
}

export function listPlatformContents(contentId: number) {
  return unwrap<PlatformContent[]>(apiClient.get(`/api/contents/${contentId}/platform-contents`));
}

export function updatePlatformContent(id: number, payload: { title: string; summary?: string; body: string; tags: string[] }) {
  return unwrap<PlatformContent>(apiClient.put(`/api/platform-contents/${id}`, payload));
}
