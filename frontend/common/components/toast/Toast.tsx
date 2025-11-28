import { useEffect } from 'react';
import { AiOutlineCheckCircle, AiOutlineCloseCircle, AiOutlineInfoCircle, AiOutlineWarning, AiOutlineClose } from 'react-icons/ai';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

export interface ToastProps {
  message: string;
  type: ToastType;
  duration?: number;
  onClose: () => void;
}

const Toast = ({ message, type, duration = 3000, onClose }: ToastProps) => {
  useEffect(() => {
    if (duration > 0) {
      const timer = setTimeout(() => {
        onClose();
      }, duration);

      return () => clearTimeout(timer);
    }
  }, [duration, onClose]);

  const icons = {
    success: <AiOutlineCheckCircle size={24} />,
    error: <AiOutlineCloseCircle size={24} />,
    info: <AiOutlineInfoCircle size={24} />,
    warning: <AiOutlineWarning size={24} />,
  };

  const styles = {
    success: 'bg-green-50 text-green-800 border-green-200',
    error: 'bg-red-50 text-red-800 border-red-200',
    info: 'bg-blue-50 text-blue-800 border-blue-200',
    warning: 'bg-yellow-50 text-yellow-800 border-yellow-200',
  };

  const iconColors = {
    success: 'text-green-600',
    error: 'text-red-600',
    info: 'text-blue-600',
    warning: 'text-yellow-600',
  };

  return (
    <div
      className={`pointer-events-auto flex w-full max-w-md items-start gap-3 rounded-lg border p-4 shadow-lg ${styles[type]}`}
      role="alert"
    >
      <div className={`flex-shrink-0 ${iconColors[type]}`}>
        {icons[type]}
      </div>
      <div className="flex-1">
        <p className="text-sm font-medium">{message}</p>
      </div>
      <button
        onClick={onClose}
        className="flex-shrink-0 opacity-70 transition-opacity hover:opacity-100"
        aria-label="Close"
      >
        <AiOutlineClose size={18} />
      </button>
    </div>
  );
};

export default Toast;
