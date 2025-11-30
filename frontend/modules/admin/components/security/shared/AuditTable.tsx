import React from 'react';

export interface AuditTableColumn<T> {
  key: string;
  label: string;
  render?: (value: any, row: T) => React.ReactNode;
  width?: string;
}

interface AuditTableProps<T> {
  columns: AuditTableColumn<T>[];
  data: T[];
  emptyMessage?: string;
  isLoading?: boolean;
  maxHeight?: string;
}

export function AuditTable<T extends Record<string, any>>({
  columns,
  data,
  emptyMessage = 'No records found',
  isLoading = false,
  maxHeight = '600px',
}: AuditTableProps<T>) {
  if (isLoading) {
    return (
      <div className="bg-white rounded-3xl border border-zinc-200 p-12 shadow-sm">
        <div className="flex flex-col items-center justify-center gap-3">
          <div className="w-8 h-8 border-2 border-zinc-200 border-t-zinc-900 rounded-full animate-spin" />
          <p className="text-sm text-zinc-500">Loading...</p>
        </div>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div className="bg-white rounded-3xl border border-zinc-200 p-12 shadow-sm">
        <div className="text-center">
          <p className="text-zinc-500">{emptyMessage}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-3xl border border-zinc-200 overflow-hidden shadow-sm">
      <div style={{ maxHeight }} className="overflow-auto">
        <table className="w-full">
          <thead className="bg-zinc-50 sticky top-0 z-10">
            <tr>
              {columns.map((column) => (
                <th
                  key={column.key}
                  className="px-6 py-4 text-left text-xs font-semibold text-zinc-500 uppercase tracking-wider"
                  style={{ width: column.width }}
                >
                  {column.label}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-200">
            {data.map((row, rowIndex) => (
              <tr key={rowIndex} className="hover:bg-zinc-50 transition-colors">
                {columns.map((column) => (
                  <td key={column.key} className="px-6 py-4 text-sm text-zinc-900">
                    {column.render
                      ? column.render(row[column.key], row)
                      : row[column.key] || '-'}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
