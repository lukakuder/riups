FROM node:24-slim
WORKDIR /api

RUN corepack enable

COPY package.json tsconfig.json yarn.lock ./
RUN yarn install --frozen-lockfile

CMD ["yarn", "dev"]


