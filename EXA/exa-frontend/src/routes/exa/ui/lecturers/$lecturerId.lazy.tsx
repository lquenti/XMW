import { createLazyFileRoute, Link } from '@tanstack/react-router'
import { useCourses } from '../../../../lib/use-courses'
import { useExams } from '../../../../lib/use-exams'
import { useLecturers } from '../../../../lib/use-lecturers'
import { useLectures } from '../../../../lib/use-lectures'
import { parseCoursesXml, parseExamsXml, parseLecturersXml, parseLecturesXml } from '../../../../lib/utils'

export const Route = createLazyFileRoute('/exa/ui/lecturers/$lecturerId')({
    component: LecturerProfileComponent,
})

function LecturerProfileComponent() {
    const { lecturerId } = Route.useParams()
    const { data: coursesData, isLoading: coursesLoading } = useCourses()
    const { data: lecturersData, isLoading: lecturersLoading } = useLecturers()
    const { data: examsData, isLoading: examsLoading } = useExams()
    const { data: lecturesData, isLoading: lecturesLoading } = useLectures()

    if (coursesLoading || lecturersLoading || examsLoading || lecturesLoading) {
        return (
            <div className="flex items-center justify-center min-h-[400px]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    if (!coursesData || !lecturersData || !examsData || !lecturesData) {
        return (
            <div className="p-4 bg-yellow-50 text-yellow-600 rounded-lg">
                No data available
            </div>
        )
    }

    const courses = parseCoursesXml(coursesData)
    const lecturers = parseLecturersXml(lecturersData)
    const exams = parseExamsXml(examsData)
    const lectures = parseLecturesXml(lecturesData)

    const lecturer = lecturers.find(l => l.id === lecturerId)
    const lecturerCourses = courses.filter(c => c.lecturer === lecturerId)
    const lecturerExams = exams.filter(e => lecturerCourses.some(c => c.id === e.course))
    const lecturerLectures = lectures.filter(l => lecturerCourses.some(c => c.id === l.course))

    // Helper function to parse date strings
    const parseDate = (dateStr: string | null): Date | null => {
        if (!dateStr) return null
        try {
            // First try parsing as a normal ISO date
            const normalDate = new Date(dateStr)
            if (!isNaN(normalDate.getTime())) {
                return normalDate
            }

            // If that fails, try handling the format with extra T
            const match = dateStr.match(/^(\d{4}-\d{2}-\d{2})T(\d+)T:(\d{2}):(\d{2})$/)
            if (!match) return null

            const [, datePart, hours, minutes, seconds] = match
            // Pad hours with leading zero if needed
            const paddedHours = hours.padStart(2, '0')
            const timeStr = `${paddedHours}:${minutes}:${seconds}`

            const cleanDate = `${datePart} ${timeStr}`
            const date = new Date(cleanDate)
            return isNaN(date.getTime()) ? null : date
        } catch (error) {
            console.error('Error parsing date:', error)
            return null
        }
    }

    // Helper function to format dates
    const formatDateTime = (date: Date | null): string => {
        if (!date) return 'Date not set'
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: '2-digit'
        })
    }

    // Helper function to determine semester from date
    const getSemester = (date: Date | null): string => {
        if (!date) return 'Unknown Semester'
        const year = date.getFullYear()
        const month = date.getMonth()
        return month >= 9 || month < 2 ? `Winter ${year}/${year + 1}` : `Summer ${year}`
    }

    // Helper function to get day of week (0 = Sunday, 6 = Saturday)
    const getDayOfWeek = (date: Date): number => {
        return date.getDay()
    }

    // Helper function to get hour (0-23)
    const getHour = (date: Date): number => {
        return date.getHours()
    }

    // Create weekly schedule
    const weeklySchedule = lecturerLectures.reduce((acc, lecture) => {
        const startDate = parseDate(lecture.start)
        const endDate = parseDate(lecture.end)
        if (!startDate || !endDate) return acc

        const dayOfWeek = getDayOfWeek(startDate)
        const startHour = getHour(startDate)
        const endHour = getHour(endDate)
        const course = lecturerCourses.find(c => c.id === lecture.course)

        if (!acc[dayOfWeek]) {
            acc[dayOfWeek] = []
        }

        acc[dayOfWeek].push({
            type: 'lecture',
            startHour,
            endHour,
            course,
            location: lecture.roomOrLink ?? 'No location set',
            isOnline: lecture.roomOrLink?.startsWith('http') ?? false,
            examId: null
        })

        return acc
    }, {} as Record<number, Array<{
        type: 'lecture' | 'exam',
        startHour: number,
        endHour: number,
        course: typeof lecturerCourses[0] | undefined,
        location: string,
        isOnline: boolean,
        examId: string | null
    }>>)

    // Add exams to weekly schedule
    lecturerExams.forEach(exam => {
        const examDate = parseDate(exam.date)
        if (!examDate) return

        const dayOfWeek = getDayOfWeek(examDate)
        const hour = getHour(examDate)
        const course = lecturerCourses.find(c => c.id === exam.course)

        if (!weeklySchedule[dayOfWeek]) {
            weeklySchedule[dayOfWeek] = []
        }

        weeklySchedule[dayOfWeek].push({
            type: 'exam',
            startHour: hour,
            endHour: hour + 2, // Assume 2-hour duration for exams
            course,
            location: exam.roomOrLink ?? 'No location set',
            isOnline: exam.isOnline,
            examId: exam.id
        })
    })

    // Sort events within each day by start hour
    Object.keys(weeklySchedule).forEach(day => {
        weeklySchedule[Number(day)].sort((a, b) => a.startHour - b.startHour)
    })

    const weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']

    // Group exams by semester
    const examsBySemester = lecturerExams.reduce((acc, exam) => {
        const examDate = parseDate(exam.date)
        const semester = getSemester(examDate)
        if (!acc[semester]) {
            acc[semester] = []
        }
        acc[semester].push(exam)
        return acc
    }, {} as Record<string, typeof lecturerExams>)

    // Sort semesters chronologically (most recent first)
    const sortedSemesters = Object.keys(examsBySemester).sort().reverse()

    if (!lecturer) {
        return (
            <div className="p-4 bg-red-50 text-red-600 rounded-lg">
                Lecturer not found
            </div>
        )
    }

    return (
        <div className="max-w-7xl mx-auto p-6">
            <div className="mb-8">
                <Link
                    to="/exa/ui/courses"
                    className="text-gray-600 hover:text-gray-900 transition-colors flex items-center gap-2 mb-4"
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
                    </svg>
                    Back to Courses
                </Link>

                <div className="bg-white rounded-xl p-8 shadow-sm">
                    <div className="flex items-start space-x-6 mb-8">
                        <img
                            src={`https://i.pravatar.cc/150?u=${lecturer.id}`}
                            alt={`${lecturer.firstname} ${lecturer.name}`}
                            className="w-24 h-24 rounded-full object-cover"
                        />
                        <div>
                            <h1 className="text-3xl font-bold text-gray-900 mb-2">
                                {lecturer.firstname} {lecturer.name}
                            </h1>
                            <div className="space-y-2">
                                <div className="flex items-center text-gray-600">
                                    <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                    </svg>
                                    {lecturer.faculty ?? 'No Faculty'}
                                </div>
                                <div className="flex items-center text-gray-600">
                                    <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
                                    </svg>
                                    {lecturer.username}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="mb-8">
                        <h2 className="text-xl font-semibold text-gray-900 mb-4">Weekly Schedule</h2>
                        <div className="bg-white rounded-xl p-6 shadow-sm">
                            <div className="grid grid-cols-7 gap-4">
                                {weekDays.map((day, index) => (
                                    <div key={day} className="space-y-4">
                                        <h3 className="text-lg font-semibold text-gray-900 pb-2 border-b">
                                            {day}
                                        </h3>
                                        <div className="space-y-2">
                                            {weeklySchedule[index]?.map((event, eventIndex) => (
                                                <Link
                                                    key={eventIndex}
                                                    to={event.type === 'exam' ? '/exa/ui/exams/$examId' : '/exa/ui/courses/$courseId'}
                                                    params={event.type === 'exam'
                                                        ? { examId: event.examId ?? '' }
                                                        : { courseId: event.course?.id ?? '' }
                                                    }
                                                    className={`block p-2 rounded-lg text-sm ${event.type === 'exam'
                                                        ? 'bg-purple-50 border border-purple-200 hover:border-purple-300'
                                                        : 'bg-blue-50 border border-blue-200 hover:border-blue-300'
                                                        } transition-colors`}
                                                >
                                                    <div className="font-medium text-gray-900">
                                                        {event.course?.name ?? 'Unknown Course'}
                                                    </div>
                                                    <div className="text-gray-600">
                                                        {event.startHour}:00 - {event.endHour}:00
                                                    </div>
                                                    <div className="text-gray-600 truncate">
                                                        {event.isOnline ? 'Online' : event.location}
                                                    </div>
                                                    <div className="text-xs font-medium mt-1">
                                                        {event.type === 'exam' ? 'Exam' : 'Lecture'}
                                                    </div>
                                                </Link>
                                            ))}
                                            {!weeklySchedule[index]?.length && (
                                                <div className="text-sm text-gray-500 py-2">
                                                    No events
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 gap-8">
                        <div>
                            <h2 className="text-xl font-semibold text-gray-900 mb-4">Courses</h2>
                            {lecturerCourses.length > 0 ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {lecturerCourses.map(course => (
                                        <Link
                                            key={course.id}
                                            to="/exa/ui/courses/$courseId"
                                            params={{ courseId: course.id ?? '' }}
                                            className="block p-4 rounded-lg border border-gray-200 hover:border-blue-300 transition-colors"
                                        >
                                            <h3 className="text-lg font-semibold text-gray-900 mb-2">
                                                {course.name}
                                            </h3>
                                            <div className="flex items-center justify-between">
                                                <span className="text-gray-600">
                                                    {course.faculty}
                                                </span>
                                                <span className="text-gray-600">
                                                    Max: {course.maxStudents} students
                                                </span>
                                            </div>
                                        </Link>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-gray-500">No courses assigned</p>
                            )}
                        </div>

                        <div>
                            <h2 className="text-xl font-semibold text-gray-900 mb-4">Exams by Semester</h2>
                            {sortedSemesters.length > 0 ? (
                                <div className="space-y-8">
                                    {sortedSemesters.map(semester => (
                                        <div key={semester}>
                                            <h3 className="text-lg font-semibold text-gray-900 mb-4 bg-gray-50 p-2 rounded">
                                                {semester}
                                            </h3>
                                            <div className="space-y-4">
                                                {examsBySemester[semester].map(exam => {
                                                    const examDate = parseDate(exam.date)
                                                    const isUpcoming = examDate ? examDate >= new Date() : false
                                                    const course = courses.find(c => c.id === exam.course)

                                                    return (
                                                        <Link
                                                            key={exam.id}
                                                            to="/exa/ui/exams/$examId"
                                                            params={{ examId: exam.id ?? '' }}
                                                            className={`block p-4 rounded-lg border ${isUpcoming ? 'bg-white border-blue-200 hover:border-blue-300' : 'bg-gray-50 border-gray-200 hover:border-gray-300'} transition-colors`}
                                                        >
                                                            <div className="flex justify-between items-start mb-2">
                                                                <div>
                                                                    <span className={`inline-block px-3 py-1 rounded-full text-sm mb-2 ${exam.isWritten ? 'bg-purple-100 text-purple-700' : 'bg-green-100 text-green-700'}`}>
                                                                        {exam.isWritten ? 'Written' : 'Oral'}
                                                                    </span>
                                                                    <h4 className="text-lg font-semibold text-gray-900">
                                                                        {course?.name ?? 'Unknown Course'} Exam
                                                                    </h4>
                                                                </div>
                                                                <span className={`text-sm ${isUpcoming ? 'text-blue-600' : 'text-gray-500'}`}>
                                                                    {formatDateTime(examDate)}
                                                                </span>
                                                            </div>
                                                            <div className="text-gray-600">
                                                                {exam.isOnline ? 'Online' : 'On-site'} - {exam.roomOrLink}
                                                            </div>
                                                        </Link>
                                                    )
                                                })}
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p className="text-gray-500">No exams scheduled</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
} 