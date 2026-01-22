import { FormControl } from "@angular/forms";

export interface userFormDto {
    username: FormControl<string | null>,
    password: FormControl<string | null>,
}

export interface userDto {
    username: string,
    password: string,
}