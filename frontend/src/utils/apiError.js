export function getApiErrorMessage(error) {
    return error?.response?.data?.message || 'Something went wrong. Please try again.';
}

export function getApiFieldErrors(error) {
    return error?.response?.data?.fieldErrors || null;
}