"use client";

import { useCallback, useState } from "react";
import { useRouter } from "next/navigation";

type UploadStatus = "idle" | "uploading" | "error" | "done";

/**
 * Uploads a challenge submission via XHR so the UI can show real upload
 * progress, posting to the same-origin proxy route. On success it refreshes the
 * server component so the new entry appears.
 */
export function useSubmissionUpload(challengeId: string) {
  const router = useRouter();
  const [status, setStatus] = useState<UploadStatus>("idle");
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState<string | null>(null);

  const upload = useCallback(
    (file: File) => {
      setStatus("uploading");
      setProgress(0);
      setError(null);

      const form = new FormData();
      form.append("file", file);

      const xhr = new XMLHttpRequest();
      xhr.open("POST", `/api/challenges/${challengeId}/submissions`);

      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          setProgress(Math.round((event.loaded / event.total) * 100));
        }
      };

      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          setProgress(100);
          setStatus("done");
          router.refresh();
        } else {
          let message = "Upload failed. Please try again.";
          try {
            const json = JSON.parse(xhr.responseText);
            if (json?.error) message = json.error;
          } catch {
            // non-JSON error body — keep the default message
          }
          setError(message);
          setStatus("error");
        }
      };

      xhr.onerror = () => {
        setError("Network error during upload");
        setStatus("error");
      };

      xhr.send(form);
    },
    [challengeId, router],
  );

  const reset = useCallback(() => {
    setStatus("idle");
    setProgress(0);
    setError(null);
  }, []);

  return { status, progress, error, upload, reset };
}
