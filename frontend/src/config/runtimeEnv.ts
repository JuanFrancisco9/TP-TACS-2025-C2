declare global {
  interface Window {
    __APP_CONFIG__?: {
      VITE_API_BASE_URL?: string;
      VITE_R2_IMAGE_BASE_URL?: string;
    };
  }
}

type RuntimeConfig = NonNullable<Window['__APP_CONFIG__']>;

const runtimeConfig: RuntimeConfig =
  (typeof window !== 'undefined' && window.__APP_CONFIG__) || {};

const buildApiBaseUrl: string =
  import.meta.env.VITE_API_BASE_URL ?? '';
const buildR2ImageBaseUrl: string =
  import.meta.env.VITE_R2_IMAGE_BASE_URL ?? '';

const apiBaseUrl =
  runtimeConfig.VITE_API_BASE_URL?.trim() ||
  buildApiBaseUrl.trim();

const r2ImageBaseUrl =
  runtimeConfig.VITE_R2_IMAGE_BASE_URL?.trim() ||
  buildR2ImageBaseUrl.trim();

export const getApiBaseUrl = (): string => {
  if (!apiBaseUrl) {
    console.warn(
      '[runtimeEnv] API base URL is empty. Set VITE_API_BASE_URL at build time or runtime.',
    );
  }
  return apiBaseUrl;
};

export const getR2ImageBaseUrl = (): string => r2ImageBaseUrl;

export const getRuntimeConfig = (): RuntimeConfig => ({
  VITE_API_BASE_URL: apiBaseUrl,
  VITE_R2_IMAGE_BASE_URL: r2ImageBaseUrl,
});
