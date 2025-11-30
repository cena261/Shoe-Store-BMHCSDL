import React, { useState } from 'react';

interface CodeSnippetProps {
  code: string;
  language: 'sql' | 'java' | 'javascript' | 'typescript' | 'bash';
  title?: string;
  description?: string;
  showCopy?: boolean;
}

export const CodeSnippet: React.FC<CodeSnippetProps> = ({
  code,
  language,
  title,
  description,
  showCopy = true,
}) => {
  const [copied, setCopied] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(code);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const languageLabels = {
    sql: 'SQL',
    java: 'Java',
    javascript: 'JavaScript',
    typescript: 'TypeScript',
    bash: 'Bash',
  };

  return (
    <div className="bg-white rounded-3xl border border-zinc-200 overflow-hidden shadow-sm">
      {(title || description) && (
        <div className="px-6 py-4 border-b border-zinc-200 bg-zinc-50">
          {title && <h4 className="text-sm font-semibold text-zinc-900 mb-1">{title}</h4>}
          {description && <p className="text-xs text-zinc-500">{description}</p>}
        </div>
      )}

      <div className="relative bg-zinc-900">
        <div className="absolute top-3 right-3 flex items-center gap-2">
          <span className="text-xs text-zinc-500 font-mono">{languageLabels[language]}</span>
          {showCopy && (
            <button
              onClick={handleCopy}
              className="px-3 py-1 bg-zinc-800 hover:bg-zinc-700 text-zinc-400 hover:text-white rounded-lg text-xs font-medium transition-colors border border-zinc-700"
            >
              {copied ? 'Copied!' : 'Copy'}
            </button>
          )}
        </div>

        <pre className="p-6 overflow-x-auto">
          <code className="text-sm text-zinc-300 font-mono leading-relaxed">{code}</code>
        </pre>
      </div>
    </div>
  );
};
