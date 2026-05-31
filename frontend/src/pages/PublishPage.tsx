import { Button, Card, Checkbox, DatePicker, Select, Space, Table, Tag, Typography, message } from 'antd';
import type { Dayjs } from 'dayjs';
import { RefreshCcw, Rocket } from 'lucide-react';
import { useEffect, useMemo, useRef, useState } from 'react';
import { listContents } from '../api/contentApi';
import { listPlatformCapabilities } from '../api/platformCapabilityApi';
import { listPlatformContents } from '../api/platformContentApi';
import { createPublishBatch, getPublishBatch, retryTask } from '../api/publishApi';
import TaskStatusTag from '../components/TaskStatusTag';
import type { ContentItem, Platform, PlatformCapability, PlatformContent, PublishBatch, PublishMode } from '../types';

const finishedStatuses = ['ALL_SUCCESS', 'PARTIAL_SUCCESS', 'ALL_FAILED'];

const publishModeColor: Record<PublishMode, string> = {
  MOCK: 'default',
  API: 'green',
  MANUAL: 'orange',
  CLIENT_ASSISTED: 'blue'
};

export default function PublishPage() {
  const [contents, setContents] = useState<ContentItem[]>([]);
  const [capabilities, setCapabilities] = useState<PlatformCapability[]>([]);
  const [platformContents, setPlatformContents] = useState<PlatformContent[]>([]);
  const [selectedContentId, setSelectedContentId] = useState<number>();
  const [selectedPlatforms, setSelectedPlatforms] = useState<Platform[]>([]);
  const [scheduledAt, setScheduledAt] = useState<Dayjs | null>(null);
  const [batch, setBatch] = useState<PublishBatch | null>(null);
  const [loading, setLoading] = useState(false);
  const pollingRef = useRef<number>();

  const generatedPlatforms = useMemo(
    () => new Set(platformContents.map((item) => item.platform)),
    [platformContents]
  );

  const platformOptions = useMemo(
    () => capabilities.map((capability) => ({
      label: (
        <Space size={6}>
          <span>{capability.displayName}</span>
          <Tag color={publishModeColor[capability.publishMode]}>{capability.publishMode}</Tag>
        </Space>
      ),
      value: capability.platform,
      disabled: !generatedPlatforms.has(capability.platform) || (!!scheduledAt && !capability.supportsSchedule)
    })),
    [capabilities, generatedPlatforms, scheduledAt]
  );

  const capabilityByPlatform = useMemo(
    () => new Map(capabilities.map((capability) => [capability.platform, capability])),
    [capabilities]
  );

  async function loadContents() {
    const page = await listContents(0, 100);
    setContents(page.content);
    if (!selectedContentId && page.content[0]) {
      setSelectedContentId(page.content[0].id);
    }
  }

  async function loadPlatformContents(contentId: number) {
    const data = await listPlatformContents(contentId);
    setPlatformContents(data);
  }

  async function loadCapabilities() {
    const data = await listPlatformCapabilities();
    setCapabilities(data);
    setSelectedPlatforms((current) => current.length > 0 ? current : data.map((item) => item.platform));
  }

  useEffect(() => {
    loadContents().catch((error) => message.error(error instanceof Error ? error.message : 'Failed to load content'));
    loadCapabilities().catch((error) => message.error(error instanceof Error ? error.message : 'Failed to load platform capabilities'));
    return () => {
      if (pollingRef.current) {
        window.clearInterval(pollingRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (selectedContentId) {
      loadPlatformContents(selectedContentId).catch(() => setPlatformContents([]));
    }
  }, [selectedContentId]);

  function startPolling(batchId: number) {
    if (pollingRef.current) {
      window.clearInterval(pollingRef.current);
    }
    pollingRef.current = window.setInterval(async () => {
      try {
        const next = await getPublishBatch(batchId);
        setBatch(next);
        if (finishedStatuses.includes(next.status) && pollingRef.current) {
          window.clearInterval(pollingRef.current);
        }
      } catch (error) {
        message.error(error instanceof Error ? error.message : 'Failed to refresh publish status');
      }
    }, 2000);
  }

  async function publish() {
    if (!selectedContentId) {
      message.warning('Select content first');
      return;
    }
    if (selectedPlatforms.length === 0) {
      message.warning('Select at least one platform');
      return;
    }
    const missing = selectedPlatforms.filter((platform) => !generatedPlatforms.has(platform));
    if (missing.length > 0) {
      message.warning(`Generate adapted content first: ${missing.join(', ')}`);
      return;
    }
    const unsupportedSchedule = scheduledAt
      ? selectedPlatforms.filter((platform) => !capabilityByPlatform.get(platform)?.supportsSchedule)
      : [];
    if (unsupportedSchedule.length > 0) {
      message.warning(`Scheduled publish is not supported for: ${unsupportedSchedule.join(', ')}`);
      return;
    }
    setLoading(true);
    try {
      const created = await createPublishBatch({
        contentId: selectedContentId,
        requestId: `web-${selectedContentId}-${Date.now()}`,
        platforms: selectedPlatforms,
        scheduledAt: scheduledAt?.toISOString()
      });
      setBatch(created);
      startPolling(created.id);
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to create publish batch');
    } finally {
      setLoading(false);
    }
  }

  async function retry(taskId: number) {
    try {
      await retryTask(taskId);
      if (batch) {
        const refreshed = await getPublishBatch(batch.id);
        setBatch(refreshed);
        startPolling(refreshed.id);
      }
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Failed to retry task');
    }
  }

  return (
    <Space direction="vertical" size={16} className="page-stack">
      <div className="page-toolbar">
        <div>
          <Typography.Title level={3}>Publish center</Typography.Title>
          <Typography.Text type="secondary">Create a batch, dispatch one task per platform, and track each task state.</Typography.Text>
        </div>
        <Button type="primary" icon={<Rocket size={16} />} loading={loading} onClick={publish}>
          Publish
        </Button>
      </div>

      <Card>
        <Space direction="vertical" size={14} className="wide">
          <Select
            className="content-select"
            placeholder="Select content"
            value={selectedContentId}
            onChange={setSelectedContentId}
            options={contents.map((item) => ({ value: item.id, label: `${item.title} · v${item.version}` }))}
          />
          <Checkbox.Group
            options={platformOptions}
            value={selectedPlatforms}
            onChange={(values) => setSelectedPlatforms(values as Platform[])}
          />
          <DatePicker
            showTime
            allowClear
            className="content-select"
            placeholder="Optional scheduled publish time"
            value={scheduledAt}
            onChange={(value) => {
              setScheduledAt(value);
              if (value) {
                setSelectedPlatforms((current) => current.filter((platform) => capabilityByPlatform.get(platform)?.supportsSchedule));
              }
            }}
            disabledDate={(current) => !!current && current.endOf('day').valueOf() < Date.now()}
          />
          <Typography.Text type="secondary">
            Platform modes come from the backend capability matrix. Scheduled publish only allows platforms marked as schedule-capable.
          </Typography.Text>
        </Space>
      </Card>

      {batch && (
        <Card
          title={
            <Space>
              <span>Batch #{batch.id}</span>
              <TaskStatusTag status={batch.status} />
              {batch.scheduledAt && <Typography.Text type="secondary">Scheduled for {new Date(batch.scheduledAt).toLocaleString()}</Typography.Text>}
            </Space>
          }
        >
          <Table
            rowKey="id"
            dataSource={batch.tasks}
            pagination={false}
            columns={[
              { title: 'Platform', dataIndex: 'platform', width: 150 },
              {
                title: 'Status',
                dataIndex: 'status',
                width: 150,
                render: (status) => <TaskStatusTag status={status} />
              },
              {
                title: 'Scheduled',
                dataIndex: 'scheduledAt',
                width: 190,
                render: (value?: string) => value ? new Date(value).toLocaleString() : <Typography.Text type="secondary">-</Typography.Text>
              },
              { title: 'Retries', dataIndex: 'retryCount', width: 100 },
              {
                title: 'Result',
                dataIndex: 'resultUrl',
                render: (url?: string) => url ? <Typography.Text copyable>{url}</Typography.Text> : <Typography.Text type="secondary">-</Typography.Text>
              },
              {
                title: 'Error',
                dataIndex: 'errorMessage',
                render: (error?: string) => error ? <Typography.Text type="danger">{error}</Typography.Text> : <Typography.Text type="secondary">-</Typography.Text>
              },
              {
                title: 'Action',
                width: 130,
                render: (_, task) => (
                  <Button
                    size="small"
                    icon={<RefreshCcw size={14} />}
                    disabled={task.status !== 'FAILED'}
                    onClick={() => retry(task.id)}
                  >
                    Retry
                  </Button>
                )
              }
            ]}
          />
        </Card>
      )}
    </Space>
  );
}
