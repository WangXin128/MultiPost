import { apiClient, unwrap } from './client';
import type { PlatformCapability } from '../types';

export function listPlatformCapabilities() {
  return unwrap<PlatformCapability[]>(apiClient.get('/api/platform-capabilities'));
}
