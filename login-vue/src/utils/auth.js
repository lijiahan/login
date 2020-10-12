export function setName(name) {
 return localStorage.setItem('name', name);
}
export function getName() {
 return localStorage.getItem('name');
}
export function setToken(token) {
 return localStorage.setItem('token', token);
}
export function getToken() {
 return localStorage.getItem('token');
}