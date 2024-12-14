import axios from 'axios'
import { RequestInterceptor } from './RequestInterceptor.ts'

const BACKEND_ENDPOINT = import.meta.env.VITE_BACKEND_ENDPOINT;

const instance = axios.create({
  baseURL: BACKEND_ENDPOINT,
  timeout: 5000
})

await RequestInterceptor.setRequestInterceptor(instance)
// await RequestInterceptor.setResponseInterceptor(instance)

const postRequest = async (url: string, data = {}): Promise<any> => {
  return await instance.post(url, data)
}

const getRequest = async (url: string, params = {}): Promise<any> => {
  return await instance.get(url, { params })
}

const patchRequest = async (url: string, data = {}): Promise<any> => {
  return await instance.patch(url, data)
}

const deleteRequest = async (url: string, params = {}): Promise<any> => {
  return await instance.delete(url, { params })
}

export const RequestHandler = {
  postRequest,
  getRequest,
  patchRequest,
  deleteRequest
}