import { createLazyFileRoute } from '@tanstack/react-router';
import { useCourses } from '../../../lib/use-courses';
import { useLecturers } from '../../../lib/use-lecturers';
import { useSemesters } from '../../../lib/use-semesters';
import { parseCoursesXml, parseLecturersXml, parseSemestersXml } from '../../../lib/utils';

export const Route = createLazyFileRoute('/exa/ui/courses')({
  component: RouteComponent,
})

function RouteComponent() {
  const { data, isLoading, error } = useCourses()
  const { data: semesters, isLoading: semestersLoading, error: semestersError } = useSemesters()
  const { data: lecturers, isLoading: lecturersLoading, error: lecturersError } = useLecturers()

  if (isLoading || semestersLoading || lecturersLoading) return <div>Loading...</div>
  if (error || semestersError || lecturersError) return <div>Error: {error?.message || semestersError?.message || lecturersError?.message}</div>
  if (!data || !semesters || !lecturers) return <div>No data</div>

  const courses = parseCoursesXml(data)
  const semestersList = parseSemestersXml(semesters)
  const lecturersList = parseLecturersXml(lecturers)

  // Sort courses by semester
  const sortedCourses = [...courses];
  sortedCourses.sort((a, b) => (a.semester ?? '').localeCompare(b.semester ?? ''));

  // Group courses by semester
  const coursesBySemester = sortedCourses.reduce<Record<string, typeof courses>>((acc, course) => {
    if (course.semester) {
      acc[course.semester] = acc[course.semester] || [];
      acc[course.semester].push(course);
    }
    return acc;
  }, {});

  return (
    <div className="p-4">
      <h1>Courses</h1>
      {Object.entries(coursesBySemester).map(([semester, semesterCourses]) => (
        <div key={semester} className="mb-6">
          <h2 className="text-xl font-bold mb-3">{semestersList.find(s => s.id === semester)?.name}</h2>
          <div className="grid gap-4">
            {semesterCourses.map(course => {
              const lecturer = lecturersList.find(l => l.id === course.lecturer)
              return (
                <div key={course.id} className="border p-3 rounded">
                  <h3 className="font-semibold">{course.name}</h3>
                  <p>Faculty: {course.faculty}</p>
                  <p>Max Students: {course.maxStudents}</p>
                  <p>Lecturer: {lecturer ? `${lecturer.firstname} ${lecturer.name}` : 'Unknown'}</p>
                </div>
              )
            })}
          </div>
        </div>
      ))}
    </div>
  )
}
