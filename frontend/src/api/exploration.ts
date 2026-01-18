import request from './index'
import type { ExplorationArea, ExplorationAreaFormData, ExplorationEvent, ExplorationEventFormData } from '@/types/exploration'

/**
 * 探索区域API
 */
export const explorationAreaApi = {
  /**
   * 获取所有探索区域
   */
  getList: () => {
    return request.get<ExplorationArea[]>('/admin/exploration-area/list')
  },

  /**
   * 获取探索区域详情
   */
  getDetail: (areaId: number) => {
    return request.get<ExplorationArea>(`/admin/exploration-area/${areaId}`)
  },

  /**
   * 创建探索区域
   */
  create: (data: ExplorationAreaFormData) => {
    return request.post<ExplorationArea>('/admin/exploration-area', data)
  },

  /**
   * 更新探索区域
   */
  update: (data: ExplorationAreaFormData) => {
    return request.put<ExplorationArea>('/admin/exploration-area', data)
  },

  /**
   * 删除探索区域
   */
  delete: (areaId: number) => {
    return request.delete(`/admin/exploration-area/${areaId}`)
  }
}

/**
 * 探索事件API
 */
export const explorationEventApi = {
  /**
   * 获取所有探索事件
   */
  getList: () => {
    return request.get<ExplorationEvent[]>('/admin/exploration-event/list')
  },

  /**
   * 根据区域ID获取事件列表
   */
  getByAreaId: (areaId: number) => {
    return request.get<ExplorationEvent[]>(`/admin/exploration-event/area/${areaId}`)
  },

  /**
   * 获取探索事件详情
   */
  getDetail: (eventId: number) => {
    return request.get<ExplorationEvent>(`/admin/exploration-event/${eventId}`)
  },

  /**
   * 创建探索事件
   */
  create: (data: ExplorationEventFormData) => {
    return request.post<ExplorationEvent>('/admin/exploration-event', data)
  },

  /**
   * 更新探索事件
   */
  update: (data: ExplorationEventFormData) => {
    return request.put<ExplorationEvent>('/admin/exploration-event', data)
  },

  /**
   * 删除探索事件
   */
  delete: (eventId: number) => {
    return request.delete(`/admin/exploration-event/${eventId}`)
  }
}
