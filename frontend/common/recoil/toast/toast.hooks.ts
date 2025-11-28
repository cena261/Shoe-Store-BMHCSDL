import { useSetRecoilState } from 'recoil';
import { toastState } from './toast.atom';
import { ToastType } from '@/common/components/toast/Toast';

let toastIdCounter = 0;

export const useToast = () => {
  const setToasts = useSetRecoilState(toastState);

  const showToast = (message: string, type: ToastType = 'info', duration = 3000) => {
    const id = `toast-${Date.now()}-${toastIdCounter++}`;

    setToasts((prev) => [
      ...prev,
      {
        id,
        message,
        type,
        duration,
      },
    ]);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  };

  return {
    showToast,
    success: (message: string, duration?: number) => showToast(message, 'success', duration),
    error: (message: string, duration?: number) => showToast(message, 'error', duration),
    info: (message: string, duration?: number) => showToast(message, 'info', duration),
    warning: (message: string, duration?: number) => showToast(message, 'warning', duration),
    removeToast,
  };
};
