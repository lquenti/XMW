import { create } from "zustand";

enum AuthorizationState {
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
type Course = {
  id: string | null
  semester: string | null
  name: string | null
  faculty: string | null
  maxStudents: string | null
  lecturer: string | null
}

type Semester = {
  id: string | null
  name: string | null
}

type Lecturer = {
  id: string | null
  username: string | null
  firstname: string | null
  name: string | null
  faculty: string | null
}

type Exam = {
  id: string | null
  course: string | null
  date: string | null
  isOnline: boolean
  isWritten: boolean
  roomOrLink: string | null
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
