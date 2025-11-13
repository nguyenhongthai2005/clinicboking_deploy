import type { ButtonHTMLAttributes } from 'react';

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'solid' | 'ghost';
};
export default function Button({ variant='solid', className='', ...rest }: Props){
  const cls = `btn ${variant==='ghost' ? 'ghost' : ''} ${className}`;
  return <button className={cls} {...rest} />;
}
