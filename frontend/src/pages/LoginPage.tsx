import { Button, Card, Form, Input, Tabs, Typography, message } from 'antd';
import { Send } from 'lucide-react';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { login, register } from '../api/authApi';

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function submit(mode: 'login' | 'register', values: { username?: string; email: string; password: string }) {
    setLoading(true);
    try {
      if (mode === 'login') {
        await login({ email: values.email, password: values.password });
      } else {
        await register({ username: values.username || values.email.split('@')[0], email: values.email, password: values.password });
      }
      navigate('/', { replace: true });
    } catch (error) {
      message.error(error instanceof Error ? error.message : 'Authentication failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <section className="login-hero">
        <div className="brand-mark large">M</div>
        <Typography.Title>MultiPost</Typography.Title>
        <Typography.Paragraph>
          A creator workspace for turning one source draft into platform-ready posts, then publishing with backend task tracking.
        </Typography.Paragraph>
      </section>
      <Card className="login-card">
        <Tabs
          items={[
            {
              key: 'login',
              label: 'Sign in',
              children: (
                <Form layout="vertical" onFinish={(values) => submit('login', values)}>
                  <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
                    <Input placeholder="creator@example.com" />
                  </Form.Item>
                  <Form.Item name="password" label="Password" rules={[{ required: true }]}>
                    <Input.Password placeholder="Your password" />
                  </Form.Item>
                  <Button block type="primary" htmlType="submit" loading={loading} icon={<Send size={16} />}>
                    Sign in
                  </Button>
                </Form>
              )
            },
            {
              key: 'register',
              label: 'Create account',
              children: (
                <Form layout="vertical" onFinish={(values) => submit('register', values)}>
                  <Form.Item name="username" label="Name" rules={[{ required: true }, { max: 80 }]}>
                    <Input placeholder="Creator name" />
                  </Form.Item>
                  <Form.Item name="email" label="Email" rules={[{ required: true, type: 'email' }]}>
                    <Input placeholder="creator@example.com" />
                  </Form.Item>
                  <Form.Item name="password" label="Password" rules={[{ required: true, min: 6 }]}>
                    <Input.Password placeholder="At least 6 characters" />
                  </Form.Item>
                  <Button block type="primary" htmlType="submit" loading={loading} icon={<Send size={16} />}>
                    Create account
                  </Button>
                </Form>
              )
            }
          ]}
        />
      </Card>
    </div>
  );
}
