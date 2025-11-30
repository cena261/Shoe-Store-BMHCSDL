import React from 'react';

interface RequirementCardProps {
  reqNumber: number | string;
  title: string;
  description: string;
  icon?: React.ReactNode;
  status: 'implemented' | 'partial' | 'pending';
  metric?: {
    label: string;
    value: string | number;
  };
  onClick?: () => void;
}

export const RequirementCard: React.FC<RequirementCardProps> = ({
  reqNumber,
  title,
  description,
  icon,
  status,
  metric,
  onClick,
}) => {
  return (
    <div
      className={`bg-white rounded-3xl p-6 border border-zinc-200 shadow-sm transition-all ${onClick ? 'cursor-pointer hover:border-zinc-900 hover:shadow-md' : ''
        }`}
      onClick={onClick}
    >
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div>
            <div className="text-xs text-zinc-500 mb-1">Requirement {reqNumber}</div>
            <h3 className="text-lg font-semibold text-zinc-900">{title}</h3>
          </div>
        </div>
      </div>

      <p className="text-sm text-zinc-500 mb-4">{description}</p>

      {metric && (
        <div className="pt-4 border-t border-zinc-100">
          <div className="text-xs text-zinc-500 mb-1">{metric.label}</div>
          <div className="text-2xl font-bold text-zinc-900">{metric.value}</div>
        </div>
      )}
    </div>
  );
};
