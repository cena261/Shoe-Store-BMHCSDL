import React, { useState } from 'react';

interface ExplainerPanelProps {
  title: string;
  sections: {
    heading: string;
    content: string | React.ReactNode;
  }[];
  variant?: 'info' | 'warning' | 'success';
  collapsible?: boolean;
  defaultExpanded?: boolean;
}

export const ExplainerPanel: React.FC<ExplainerPanelProps> = ({
  title,
  sections,
  variant = 'info',
  collapsible = false,
  defaultExpanded = true,
}) => {
  const [isExpanded, setIsExpanded] = useState(defaultExpanded);

  const variantStyles = {
    info: 'bg-blue-50 border-blue-100',
    warning: 'bg-yellow-50 border-yellow-100',
    success: 'bg-green-50 border-green-100',
  };

  const variantIconColors = {
    info: 'text-blue-600',
    warning: 'text-yellow-600',
    success: 'text-green-600',
  };

  return (
    <div className={`rounded-3xl border ${variantStyles[variant]} overflow-hidden`}>
      <div
        className={`px-6 py-4 flex items-center justify-between ${collapsible ? 'cursor-pointer hover:bg-white/50' : ''
          }`}
        onClick={() => collapsible && setIsExpanded(!isExpanded)}
      >
        <div className="flex items-center gap-3">
          <div className={`${variantIconColors[variant]}`}>
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
              />
            </svg>
          </div>
          <h3 className="text-sm font-semibold text-zinc-900">{title}</h3>
        </div>
        {collapsible && (
          <svg
            className={`w-5 h-5 text-zinc-400 transition-transform ${isExpanded ? 'rotate-180' : ''
              }`}
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        )}
      </div>

      {isExpanded && (
        <div className="px-6 pb-6 space-y-4">
          {sections.map((section, index) => (
            <div key={index}>
              <h4 className="text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-2">
                {section.heading}
              </h4>
              {typeof section.content === 'string' ? (
                <p className="text-sm text-zinc-600 leading-relaxed">{section.content}</p>
              ) : (
                section.content
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
