# syntax=docker/dockerfile:1

FROM node:20-alpine

WORKDIR /usr/src/app

# Install dependencies using pnpm to respect the existing lockfile
COPY package.json pnpm-lock.yaml ./
RUN corepack enable && pnpm install --prod --frozen-lockfile

# Copy the rest of the bot source
COPY . .

ENV NODE_ENV=production

CMD ["node", "index.js"]
