# ----- Build Stage -----
FROM node:20-alpine AS build

WORKDIR /app

# Copia el package.json
COPY package.json pnpm-lock.yaml ./

# Instala pnpm y las dependencias en un solo paso
RUN npm install -g pnpm && \
    pnpm install

# Copia el resto del código fuente
COPY . .

# Build de la aplicación
RUN pnpm run build

# ----- Run Stage -----
FROM nginx:alpine

# Copia los archivos build al directorio de nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Copia configuración de nginx personalizada para SPA
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    root /usr/share/nginx/html; \
    index index.html; \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
