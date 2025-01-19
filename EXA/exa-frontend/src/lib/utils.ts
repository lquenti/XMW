import { create } from "zustand";

export enum AuthorizationState {
  Guest = 'Guest',
  Student  = 'Student',
  Lecturer = 'Lecturer',
  Admin = 'Admin',
}

export const useAuthorizationState = create<{
  state: AuthorizationState
  setState: (state: AuthorizationState) => void
}>((set) => ({
  state: AuthorizationState.Guest,
  setState: (state) => set({ state }),
}))

// Add new types and functions
export type Course = {
  id: string | null
  semester: string | null
  name: string | null
  faculty: string | null
  maxStudents: string | null
  lecturer: string | null
}

export type Semester = {
  id: string | null
  name: string | null
}

export type Lecturer = {
  id: string | null
  username: string | null
  firstname: string | null
  name: string | null
  faculty: string | null
}

export type Exam = {
  id: string | null
  course: string | null
  date: string | null
  isOnline: boolean
  isWritten: boolean
  roomOrLink: string | null
}

export type Lecture = {
  id: string | null
  course: string | null
  start: string | null
  end: string | null
  roomOrLink: string | null
}

export type Module = {
    id: string | null
    credits: number | null
    course: string | null
    name: string | null
    studies: string[]
    description: string | null
}

export function parseCoursesXml(xmlString: string): Course[] {
  const xml = new DOMParser().parseFromString(xmlString, 'text/xml')
  return Array.from(xml.getElementsByTagName('Course')).map(course => ({
    id: course.getAttribute('id'),
    semester: course.getAttribute('semester'),
    name: course.getElementsByTagName('name')[0]?.textContent,
    faculty: course.getElementsByTagName('faculty')[0]?.textContent,
    maxStudents: course.getElementsByTagName('max_students')[0]?.textContent,
    lecturer: course.getAttribute('lecturer'),
  }))
}

export function parseSemestersXml(xmlString: string): Semester[] {
  const xml = new DOMParser().parseFromString(xmlString, 'text/xml')
  return Array.from(xml.getElementsByTagName('Semester')).map(semester => ({
    id: semester.getAttribute('id'),
    name: semester.getElementsByTagName('name')[0]?.textContent,
  }))
}

export function parseLecturersXml(xmlString: string): Lecturer[] {
  const xml = new DOMParser().parseFromString(xmlString, 'text/xml')
  return Array.from(xml.getElementsByTagName('Lecturer')).map(lecturer => ({
    id: lecturer.getAttribute('id'),
    username: lecturer.getAttribute('username'),
    firstname: lecturer.getElementsByTagName('firstname')[0]?.textContent,
    name: lecturer.getElementsByTagName('name')[0]?.textContent,
    faculty: lecturer.getAttribute('faculty'),
  }))
}

export function parseExamsXml(xmlString: string): Exam[] {
  const xml = new DOMParser().parseFromString(xmlString, 'text/xml')
  return Array.from(xml.getElementsByTagName('Exam')).map(exam => ({
    id: exam.getAttribute('id'),
    course: exam.getAttribute('course'),
    date: exam.getElementsByTagName('date')[0]?.textContent,
    isOnline: exam.getElementsByTagName('is_online')[0]?.textContent === '1',
    isWritten: exam.getElementsByTagName('is_written')[0]?.textContent === '1',
    roomOrLink: exam.getElementsByTagName('room_or_link')[0]?.textContent,
  }))
}

export function parseLecturesXml(xmlString: string): Lecture[] {
  const xml = new DOMParser().parseFromString(xmlString, 'text/xml')
  return Array.from(xml.getElementsByTagName('Lecture')).map(lecture => ({
    id: lecture.getAttribute('id'),
    course: lecture.getAttribute('course'),
    start: lecture.getElementsByTagName('start')[0]?.textContent,
    end: lecture.getElementsByTagName('end')[0]?.textContent,
    roomOrLink: lecture.getElementsByTagName('room_or_link')[0]?.textContent,
  }))
}

export function parseModulesXml(xml: string): Module[] {
    const parser = new DOMParser()
    const doc = parser.parseFromString(xml, 'text/xml')
    const modules = doc.getElementsByTagName('Module')

    return Array.from(modules).map(module => {
        const studies = Array.from(module.getElementsByTagName('Study')).map(study => study.textContent?.trim() ?? '')
        
        return {
            id: module.getAttribute('id'),
            credits: Number(module.getAttribute('credits')) || null,
            course: module.getAttribute('course'),
            name: module.getElementsByTagName('name')[0]?.textContent?.trim() ?? null,
            studies,
            description: module.getElementsByTagName('Description')[0]?.textContent?.trim() ?? null
        }
    })
}
