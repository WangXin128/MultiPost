export type ContentType = 'ARTICLE' | 'VIDEO_SCRIPT' | 'NOTE';
export type ContentStatus = 'DRAFT' | 'READY' | 'ARCHIVED';
export type Platform = 'WECHAT' | 'ZHIHU' | 'BILIBILI' | 'XIAOHONGSHU';
export type PublishTaskStatus = 'PENDING' | 'PUBLISHING' | 'SUCCESS' | 'FAILED' | 'RETRYING' | 'UNKNOWN';
export type PublishBatchStatus = 'PUBLISHING' | 'ALL_SUCCESS' | 'PARTIAL_SUCCESS' | 'ALL_FAILED';

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface AuthResponse {
  token: string;
  userId: number;
  email: string;
  username: string;
}

export interface CurrentUser {
  id: number;
  email: string;
  username: string;
}

export interface ContentItem {
  id: number;
  title: string;
  summary?: string;
  body: string;
  tags?: string;
  coverUrl?: string;
  contentType: ContentType;
  status: ContentStatus;
  version: number;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface PlatformContent {
  id: number;
  contentId: number;
  platform: Platform;
  title: string;
  summary?: string;
  body: string;
  tags: string[];
  sourceVersion: number;
  createdAt: string;
  updatedAt: string;
}

export interface PublishTask {
  id: number;
  batchId: number;
  platformContentId: number;
  platform: Platform;
  status: PublishTaskStatus;
  retryCount: number;
  resultUrl?: string;
  errorMessage?: string;
  publishedAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PublishBatch {
  id: number;
  contentId: number;
  requestId: string;
  status: PublishBatchStatus;
  taskCount: number;
  tasks: PublishTask[];
  createdAt: string;
  updatedAt: string;
}
