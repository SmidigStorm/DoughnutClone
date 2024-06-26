/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AudioUploadDTO } from '../models/AudioUploadDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class RestAiAudioControllerService {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * @param formData
     * @returns string OK
     * @throws ApiError
     */
    public convertSrt(
        formData?: AudioUploadDTO,
    ): CancelablePromise<string> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/api/notes/convertSrt',
            formData: formData,
            mediaType: 'multipart/form-data',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * @param note
     * @returns string OK
     * @throws ApiError
     */
    public convertAudioToSrt(
        note: number,
    ): CancelablePromise<string> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/api/notes/{note}/audio-to-srt',
            path: {
                'note': note,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
