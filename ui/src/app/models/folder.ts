export interface FolderDTO {
    id: number,
    name: string,
    parentId: number,
    fileCount: number,
}

export interface FileDTO {
    id: number,
    fileName: string,
    contentType: string,
    size: number,
    uploadTime: Date
}

export interface FolderResponse {
    currentFolder: FolderDTO,
    subFolders: FolderDTO[],
    files: FileDTO[]
}