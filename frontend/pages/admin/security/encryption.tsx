import React, { useEffect, useState } from 'react';
import { useRouter } from 'next/router';
import {
  SecurityMetric,
  CodeSnippet,
  ExplainerPanel,
  AuditTable,
  AuditTableColumn,
  FilterBar,
} from '@/modules/admin/components/security/shared';
import { exportAdminService } from '@/common/api/services/exportAdminService';
import { useAdminOnly } from '@/common/hooks/auth';
import type {
  OrderExportSummary,
  ExportOrdersRequest,
  ExportOrdersResponse,
} from '@/common/types/backend';

export default function EncryptionPage() {
  useAdminOnly();
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(true);
  const [exportHistory, setExportHistory] = useState<OrderExportSummary[]>([]);
  const [isCreatingExport, setIsCreatingExport] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    loadExportHistory();
  }, []);

  const loadExportHistory = async () => {
    try {
      setIsLoading(true);
      const exports = await exportAdminService.getExportHistory(50);
      setExportHistory(exports);
    } catch (error) {
      console.error('Failed to load export history:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleCreateExport = async () => {
    try {
      setIsCreatingExport(true);
      setSuccessMessage('');
      const request: ExportOrdersRequest = {};
      const response: ExportOrdersResponse = await exportAdminService.createExport(request);
      setSuccessMessage(`Export created successfully (ID: ${response.exportId})`);
      await loadExportHistory();
    } catch (error) {
      console.error('Failed to create export:', error);
    } finally {
      setIsCreatingExport(false);
    }
  };

  const handleDownloadExport = async (exportId: number) => {
    try {
      const response = await exportAdminService.downloadExport(exportId);
      const blob = new Blob([response.jsonData], { type: 'application/json' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export-${exportId}.json`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Failed to download export:', error);
    }
  };

  const exportColumns: AuditTableColumn<OrderExportSummary>[] = [
    {
      key: 'exportId',
      label: 'Export ID',
      width: '100px',
    },
    {
      key: 'createdAt',
      label: 'Created At',
      render: (value) => new Date(value).toLocaleString(),
      width: '200px',
    },
    {
      key: 'createdByEmail',
      label: 'Created By',
      width: '250px',
    },
    {
      key: 'encryptedDataSize',
      label: 'Encrypted Size',
      render: (value) => `${(value / 1024).toFixed(2)} KB`,
      width: '150px',
    },
    {
      key: 'exportId',
      label: 'Actions',
      render: (value) => (
        <button
          onClick={() => handleDownloadExport(value)}
          className="px-3 py-1 bg-white border border-zinc-200 hover:bg-zinc-50 text-zinc-900 rounded-lg text-sm transition-colors shadow-sm"
        >
          Download
        </button>
      ),
      width: '120px',
    },
  ];

  return (
    <section className="px-4 py-20 sm:px-8 bg-zinc-50 min-h-screen">
      <div className="mx-auto flex max-w-7xl flex-col gap-10">
        <div className="flex items-center justify-between">
          <div>
            <button
              onClick={() => router.push('/admin/security/overview')}
              className="text-zinc-500 hover:text-zinc-900 mb-4 flex items-center gap-2 transition-colors"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
              </svg>
              Back to Security Dashboard
            </button>
            <h1 className="text-4xl font-bold text-zinc-900 mb-2">Encryption</h1>
            <p className="text-zinc-500">
              Requirements 2, 3, 4: Symmetric (DES), Asymmetric (RSA), and Hybrid Encryption
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <SecurityMetric
            label="Symmetric (DES)"
            value="Active"
            subtext="DBMS_CRYPTO DES"
            color="blue"
          />
          <SecurityMetric
            label="Asymmetric (RSA)"
            value="Active"
            subtext="Java RSA 2048-bit"
            color="green"
          />
          <SecurityMetric
            label="Hybrid Exports"
            value={exportHistory.length}
            subtext="AES + RSA combined"
            color="yellow"
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 2: Symmetric Encryption (DES)</h2>

          <ExplainerPanel
            title="How DES Symmetric Encryption Works"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'Uses Oracle DBMS_CRYPTO package with DES algorithm for database-level encryption. The same key is used for both encryption and decryption operations.',
              },
              {
                heading: 'Use Case',
                content:
                  'Encrypts sensitive user data like phone numbers and addresses in the database. Provides confidentiality while maintaining searchability for authorized users.',
              },
            ]}
          />

          <CodeSnippet
            language="sql"
            title="DES Encryption Example (Oracle)"
            description="PL/SQL function using DBMS_CRYPTO for symmetric encryption"
            code={`CREATE OR REPLACE FUNCTION encrypt_des(p_text VARCHAR2)
RETURN RAW IS
  l_key RAW(128) := UTL_RAW.cast_to_raw('MySecretKey12345');
  l_encrypted RAW(2000);
BEGIN
  l_encrypted := DBMS_CRYPTO.ENCRYPT(
    src => UTL_RAW.cast_to_raw(p_text),
    typ => DBMS_CRYPTO.DES_CBC_PKCS5,
    key => l_key
  );
  RETURN l_encrypted;
END;`}
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 3: Asymmetric Encryption (RSA)</h2>

          <ExplainerPanel
            title="How RSA Asymmetric Encryption Works"
            variant="info"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'Uses Java RSA algorithm with 2048-bit key pairs. Public key encrypts data, private key decrypts. Keys are stored securely in application configuration.',
              },
              {
                heading: 'Use Case',
                content:
                  'Encrypts the AES symmetric key in hybrid encryption. Enables secure key exchange without sharing private keys.',
              },
            ]}
          />

          <CodeSnippet
            language="java"
            title="RSA Encryption Example (Java)"
            description="Java service using RSA for asymmetric encryption"
            code={`public class RSAEncryptionService {
    private KeyPair keyPair;

    public String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String ciphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decoded = Base64.getDecoder().decode(ciphertext);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}`}
          />
        </div>

        <div className="space-y-4">
          <h2 className="text-2xl font-bold text-zinc-900">Requirement 4: Hybrid Encryption (AES + RSA)</h2>

          <ExplainerPanel
            title="How Hybrid Encryption Works"
            variant="success"
            sections={[
              {
                heading: 'Implementation',
                content:
                  'Combines AES (fast symmetric encryption for large data) with RSA (secure asymmetric encryption for keys). Data is encrypted with AES, then the AES key is encrypted with RSA public key.',
              },
              {
                heading: 'Use Case',
                content:
                  'Used for order data exports. Provides both performance (AES for bulk data) and security (RSA for key exchange). The encrypted export can only be decrypted with the RSA private key.',
              },
            ]}
          />

          <CodeSnippet
            language="java"
            title="Hybrid Encryption Example (Java)"
            description="Order export service combining AES + RSA"
            code={`public OrderExport createEncryptedExport(List<Order> orders) {
    // 1. Generate random AES key
    SecretKey aesKey = KeyGenerator.getInstance("AES").generateKey();

    // 2. Encrypt order data with AES
    String jsonData = objectMapper.writeValueAsString(orders);
    byte[] encryptedData = aesEncrypt(jsonData, aesKey);

    // 3. Encrypt AES key with RSA public key
    byte[] encryptedAesKey = rsaEncrypt(aesKey.getEncoded());

    // 4. Store both in database
    return OrderExport.builder()
        .encryptedData(encryptedData)
        .encryptedAesKey(encryptedAesKey)
        .build();
}`}
          />

          <div className="flex items-center gap-4">
            <button
              onClick={handleCreateExport}
              disabled={isCreatingExport}
              className="px-6 py-3 bg-zinc-900 text-white rounded-2xl font-semibold hover:bg-zinc-800 transition-colors disabled:opacity-50 disabled:cursor-not-allowed shadow-sm"
            >
              {isCreatingExport ? 'Creating Export...' : 'Create New Encrypted Export'}
            </button>
            {successMessage && (
              <span className="text-green-600 text-sm">{successMessage}</span>
            )}
          </div>

          <AuditTable
            columns={exportColumns}
            data={exportHistory}
            isLoading={isLoading}
            emptyMessage="No encrypted exports yet. Click 'Create New Encrypted Export' to generate one."
            maxHeight="400px"
          />
        </div>
      </div>
    </section>
  );
}
