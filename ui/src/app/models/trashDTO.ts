export interface TrashDTO {
    id: number,
    name: string,
    type: 'file' | 'folder',
    originalPath: string,
    deletedAt: string,
}

export interface PageResponse<T> {
    content: T[],
    totalElements: number,
    totalPages: number,
    size: number,
    number: number,
}