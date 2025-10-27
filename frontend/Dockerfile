# ----- Build Stage -----
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json pnpm-lock.yaml ./
RUN npm install -g pnpm && pnpm install --frozen-lockfile

COPY . .
RUN pnpm run build

# ----- Run Stage -----
FROM nginx:1.29-alpine

# Script que fija un PORT por defecto antes de renderizar el template
COPY nginx/10-set-port.envsh /docker-entrypoint.d/10-set-port.envsh

# Script que genera configuracion runtime para el frontend
COPY nginx/40-generate-app-config.sh /docker-entrypoint.d/40-generate-app-config.sh
RUN chmod +x /docker-entrypoint.d/40-generate-app-config.sh

# Usar templates del entrypoint oficial
COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template

RUN rm -f /etc/nginx/conf.d/default.conf || true

# Archivos estaticos de Vite
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx/app-config.js.template /usr/share/nginx/html/app-config.js.template
