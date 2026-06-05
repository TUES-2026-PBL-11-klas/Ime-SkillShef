"use client";

import { useState } from "react";
import { useSubmissionUpload } from "@/client/state/use-submission-upload";

export function SubmissionUploadForm({ challengeId }: { challengeId: string }) {
  const { status, progress, error, upload, reset } = useSubmissionUpload(challengeId);
  const [fileName, setFileName] = useState<string | null>(null);

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;
    setFileName(file.name);
    upload(file);
  }

  return (
    <div className="rounded-xl border border-dashed border-gray-300 p-4">
      <h3 className="font-semibold text-gray-800">Submit your result</h3>
      <p className="mt-1 text-xs text-gray-500">
        Upload a photo or short video of your dish.
      </p>

      <input
        type="file"
        accept="image/*,video/*"
        onChange={handleChange}
        disabled={status === "uploading"}
        className="mt-3 block w-full text-sm file:mr-3 file:rounded-lg file:border-0 file:bg-orange-500 file:px-3 file:py-2 file:text-sm file:font-medium file:text-white hover:file:bg-orange-600"
      />

      {status === "uploading" && (
        <div className="mt-3">
          <div className="h-2 w-full overflow-hidden rounded-full bg-gray-200">
            <div
              className="h-full bg-orange-500 transition-all"
              style={{ width: `${progress}%` }}
            />
          </div>
          <p className="mt-1 text-xs text-gray-500">
            Uploading {fileName}… {progress}%
          </p>
        </div>
      )}

      {status === "done" && (
        <p className="mt-2 text-sm text-green-600">Submitted! 🎉</p>
      )}

      {status === "error" && (
        <div className="mt-2">
          <p className="text-sm text-red-500" role="alert">
            {error}
          </p>
          <button onClick={reset} className="mt-1 text-xs text-orange-600 hover:underline">
            Try again
          </button>
        </div>
      )}
    </div>
  );
}
