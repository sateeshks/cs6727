import {fromByteArray,toByteArray} from "base64-js";

export function base64urlToUint8array(base64Bytes) {
    const padding = '===='.substring(0, (4 - (base64Bytes.length % 4)) % 4);
    return toByteArray((base64Bytes + padding).replace(/\//g, "_").replace(/\+/g, "-"));
}
export function uint8arrayToBase64url(bytes) {
    if (bytes instanceof Uint8Array) {
        return fromByteArray(bytes).replace(/\+/g, "-").replace(/\//g, "_").replace(/=/g, "");
    } else {
        return uint8arrayToBase64url(new Uint8Array(bytes));
    }
}
export class WebAuthServerError extends Error {
    constructor(foo = 'bar', ...params) {
        super(...params)
        this.name = 'ServerError'
        this.foo = foo
        this.date = new Date()
    }
}
export function throwError(response) {
    throw new WebAuthServerError("Error from client", response.body);
}
export function checkStatus(response) {
    if (response.status !== 200) {
       throw new Error( '' + response.status);
    } else {
        return response;
    }
}
export function initialCheckStatus(response) {
    checkStatus(response);

    return response.json();
}
export function followRedirect(response) {
    if (response.status === 200) {
        window.location.href = response.url;
    } else {
        throwError(response);
    }
}
export function displayError(error) {
    const errorElem = document.getElementById('errors');
    errorElem.innerHTML = error;
    console.error(error);
}
