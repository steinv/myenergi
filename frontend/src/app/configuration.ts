import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

export interface ConfigurationInterface {
    myenergi: {
        url: string,
        zappi?: string,
    }
}


@Injectable()
export class Configuraion {

    public url: string = '';
    public zappi: string = '';

    public constructor(private readonly _httpClient: HttpClient) {}

    load(path: string) {
        return this._httpClient.get<ConfigurationInterface>(path)
        .toPromise()
        .then(config => {
            this.url = config?.myenergi.url || '';
            this.zappi = config?.myenergi.zappi || '';
        });
    }
}
