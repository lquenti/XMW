import { type HTMLAttributes } from 'react'

interface ExaLogoProps extends HTMLAttributes<HTMLSpanElement> {
    size?: 'sm' | 'md' | 'lg' | 'xl'
}

const sizeClasses = {
    sm: 'text-lg',
    md: 'text-2xl',
    lg: 'text-4xl',
    xl: 'text-6xl'
}

export function ExaLogo({ size = 'md', className = '', ...props }: ExaLogoProps) {
    return (
        <span
            className={`inline-block font-mono italic font-bold ${sizeClasses[size]} ${className}`}
            style={{
                WebkitTextFillColor: 'transparent',
                WebkitBackgroundClip: 'text',
                backgroundClip: 'text',
                backgroundImage: 'url(http://www.dbis.informatik.uni-goettingen.de/Icons/ROTATINGGLOBE.GIF)',
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                color: 'transparent'
            }}
            {...props}
        >
            EXA
        </span>
    )
} 