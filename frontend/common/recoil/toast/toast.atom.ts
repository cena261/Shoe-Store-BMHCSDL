import { atom } from 'recoil';
import { ToastItem } from '@/common/components/toast/ToastContainer';

export const toastState = atom<ToastItem[]>({
  key: 'toastState',
  default: [],
});
