import React from 'react';

interface SecurityMetricProps {
  label: string;
  value: string | number;
  icon?: React.ReactNode;
  subtext?: string;
  trend?: {
    direction: 'up' | 'down' | 'neutral';
    value: string;
  };
  color?: 'blue' | 'green' | 'yellow' | 'red' | 'zinc';
}

export const SecurityMetric: React.FC<SecurityMetricProps> = ({
  label,
  value,
  icon,
  subtext,
  trend,
  color = 'zinc',
}) => {
  const colorClasses = {
    blue: 'bg-blue-100 text-blue-600',
    green: 'bg-green-100 text-green-600',
    yellow: 'bg-yellow-100 text-yellow-600',
    red: 'bg-red-100 text-red-600',
    zinc: 'bg-zinc-100 text-zinc-600',
  };

  const trendColors = {
    up: 'text-green-600',
    down: 'text-red-600',
    neutral: 'text-zinc-500',
  };

  return (
    <div className="bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm">
      <div className="flex items-center justify-between mb-4">
        {icon && (
          <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${colorClasses[color]}`}>
            {icon}
          </div>
        )}
        {trend && (
          <div className={`text-xs font-medium ${trendColors[trend.direction]}`}>
            {trend.direction === 'up' && '↑'}
            {trend.direction === 'down' && '↓'}
            {trend.value}
          </div>
        )}
      </div>

      <div className="text-sm text-zinc-500 mb-1">{label}</div>
      <div className="text-3xl font-bold text-zinc-900 mb-2 truncate" title={String(value)}>{value}</div>
      {subtext && <div className="text-xs text-zinc-500">{subtext}</div>}
    </div>
  );
};
