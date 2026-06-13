import {
  ButtonHTMLAttributes,
  InputHTMLAttributes,
  ReactNode,
  SelectHTMLAttributes,
  TextareaHTMLAttributes,
  useId,
} from "react";
import styles from "./ui.module.css";

function cx(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(" ");
}

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "danger";
  fullWidth?: boolean;
};

export function Button({ variant = "primary", fullWidth = false, className, ...props }: ButtonProps) {
  return (
    <button
      className={cx(styles.button, styles[variant], fullWidth && styles.fullWidth, className)}
      {...props}
    />
  );
}

type FieldShellProps = {
  id?: string;
  label: ReactNode;
  required?: boolean;
  error?: string;
  helpText?: ReactNode;
  className?: string;
  children: (fieldProps: {
    id: string;
    describedBy?: string;
    invalid: boolean;
  }) => ReactNode;
};

function FieldShell({ id, label, required, error, helpText, className, children }: FieldShellProps) {
  const generatedId = useId();
  const fieldId = id ?? generatedId;
  const errorId = `${fieldId}-error`;
  const helpId = `${fieldId}-help`;
  const describedBy = [error ? errorId : null, helpText ? helpId : null].filter(Boolean).join(" ") || undefined;

  return (
    <div className={cx(styles.field, className)}>
      <label className={styles.label} htmlFor={fieldId}>
        {label} {required && <span className={styles.required}>*</span>}
      </label>
      {children({ id: fieldId, describedBy, invalid: Boolean(error) })}
      {error && (
        <p id={errorId} className={styles.errorText}>
          {error}
        </p>
      )}
      {helpText && (
        <span id={helpId} className={styles.helpText}>
          {helpText}
        </span>
      )}
    </div>
  );
}

type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  trailingIcon?: ReactNode;
  fieldClassName?: string;
};

export function TextField({
  id,
  label,
  error,
  helpText,
  trailingIcon,
  fieldClassName,
  className,
  required,
  ...props
}: TextFieldProps) {
  const hasTrailingIcon = Boolean(trailingIcon);

  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <div className={styles.controlWrap}>
          <input
            id={fieldId}
            className={cx(styles.control, hasTrailingIcon && styles.withTrailing, className)}
            required={required}
            aria-invalid={invalid}
            aria-describedby={describedBy}
            {...props}
          />
          {trailingIcon && <span className={styles.trailing}>{trailingIcon}</span>}
        </div>
      )}
    </FieldShell>
  );
}

type SelectFieldProps = Omit<SelectHTMLAttributes<HTMLSelectElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  fieldClassName?: string;
};

export function SelectField({
  id,
  label,
  error,
  helpText,
  fieldClassName,
  className,
  required,
  children,
  ...props
}: SelectFieldProps) {
  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <select
          id={fieldId}
          className={cx(styles.control, className)}
          required={required}
          aria-invalid={invalid}
          aria-describedby={describedBy}
          {...props}
        >
          {children}
        </select>
      )}
    </FieldShell>
  );
}

type TextareaFieldProps = Omit<TextareaHTMLAttributes<HTMLTextAreaElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  fieldClassName?: string;
};

export function TextareaField({
  id,
  label,
  error,
  helpText,
  fieldClassName,
  className,
  required,
  ...props
}: TextareaFieldProps) {
  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <textarea
          id={fieldId}
          className={cx(styles.control, styles.textarea, className)}
          required={required}
          aria-invalid={invalid}
          aria-describedby={describedBy}
          {...props}
        />
      )}
    </FieldShell>
  );
}

type CardProps = {
  title?: ReactNode;
  subtitle?: ReactNode;
  children: ReactNode;
  className?: string;
};

export function Card({ title, subtitle, children, className }: CardProps) {
  return (
    <div className={cx(styles.card, className)}>
      {(title || subtitle) && (
        <div className={styles.cardHeader}>
          {title && <h2 className={styles.cardTitle}>{title}</h2>}
          {subtitle && <p className={styles.cardSubtitle}>{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  );
}

type BadgeProps = {
  children: ReactNode;
  variant?: "success" | "danger" | "neutral";
  className?: string;
};

export function Badge({ children, variant = "neutral", className }: BadgeProps) {
  const variantClass = {
    success: styles.badgeSuccess,
    danger: styles.badgeDanger,
    neutral: styles.badgeNeutral,
  }[variant];

  return <span className={cx(styles.badge, variantClass, className)}>{children}</span>;
}

export function FormActions({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={cx(styles.formActions, className)}>{children}</div>;
}
